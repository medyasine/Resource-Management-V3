package org.example.ecom.ManagedBeans;

import org.example.ecom.Service.CategoryDaoImp;
import org.example.ecom.Service.ProductDaoImpl;
import org.example.ecom.model.Category;
import org.example.ecom.model.Product;
import org.primefaces.model.file.UploadedFile;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.Part;
import java.io.*;
import java.util.List;
import java.util.UUID;

@ManagedBean(name = "adminProducts")
@ViewScoped
public class AdminProducts implements Serializable {
    private static final long serialVersionUID = 1L;

    private final ProductDaoImpl productDaoImp = new ProductDaoImpl();
    private final CategoryDaoImp categoryDaoImp = new CategoryDaoImp();

    private List<Product> listProducts;
    private List<Product> filteredProducts;
    private List<Category> categoryList;
    private Product product = new Product();
    private Long selectedCategoryId;
    private UploadedFile imageFile;

    // Get the real path to your webapp's upload directory
    private static final String IMAGE_UPLOAD_DIR = FacesContext.getCurrentInstance()
            .getExternalContext().getRealPath("/resources/images");

    public AdminProducts() {
        loadCategories();
        createUploadDirectory();
    }

    private void createUploadDirectory() {
        try {
            File uploadDir = new File(IMAGE_UPLOAD_DIR);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
        } catch (Exception e) {
            System.err.println("Error creating upload directory: " + e.getMessage());
        }
    }

    private void loadCategories() {
        try {
            this.categoryList = categoryDaoImp.getAll();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error loading categories", e.getMessage()));
        }
    }

    public void prepareNewProduct() {
        this.product = new Product();
        this.selectedCategoryId = null;
        this.imageFile = null;
    }

    public void setProduct(Product product) {
        try {
            this.product = new Product();
            this.product.setId(product.getId());
            this.product.setName(product.getName());
            this.product.setDescription(product.getDescription());
            this.product.setPrice(product.getPrice());
            this.product.setQuantity(product.getQuantity());
            this.product.setCategory(product.getCategory());
            this.product.setImage(product.getImage()); // Keep the existing image if not updated
            this.selectedCategoryId = product.getCategory().getId();
            this.imageFile = null;
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error setting product", e.getMessage()));
        }
    }

    public void saveProduct() {
        try {
            // Set category
            if (selectedCategoryId != null) {
                Category selectedCategory = categoryDaoImp.getById(selectedCategoryId)
                        .orElseThrow(() -> new RuntimeException("Category not found"));
                product.setCategory(selectedCategory);
            } else {
                throw new RuntimeException("Category is required");
            }

            // Handle image upload
            if (imageFile != null && imageFile.getSize() > 0) {
                // Delete old image if exists and updating
                if (product.getId() != null && product.getImage() != null) {
                    deleteOldImage(product.getImage());
                }

//                // Save new image
                String imageName = saveImageFile(imageFile);
//                System.out.println("Image saved: " + imageName);
                if (imageName != null) {
                    product.setImage(imageName);
                }
            }

            // Save or update product
            if (product.getId() != null) {
                productDaoImp.update(product);
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Product updated successfully"));
            } else {
                productDaoImp.create(product);
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Product created successfully"));
            }

            // Refresh the products list
            this.listProducts = productDaoImp.getAll();

            // Reset form
            this.product = new Product();
            this.selectedCategoryId = null;
            this.imageFile = null;

        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error saving product", e.getMessage()));
            e.printStackTrace();
        }
    }

    private void deleteOldImage(String imageName) {
        try {
            File oldFile = new File(IMAGE_UPLOAD_DIR + File.separator + imageName);
            System.out.println("Deleting old image: " + oldFile.getAbsolutePath());
            if (oldFile.exists()) {
                oldFile.delete();
            }
        } catch (Exception e) {
            System.err.println("Error deleting old image: " + e.getMessage());
        }
    }

    private String saveImageFile(UploadedFile file) {
        if (file != null && file.getSize() > 0) {
            // Validate MIME type manually
            String mimeType = file.getContentType();
            if (!isValidMimeType(mimeType)) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid file type", "Only GIF, JPEG, and PNG are allowed."));
                return null;
            }

            String fileName = UUID.randomUUID() + "_" + file.getFileName();
            String fullPath = IMAGE_UPLOAD_DIR + File.separator + fileName;
            try (InputStream input = file.getInputStream();
                 FileOutputStream output = new FileOutputStream(fullPath)) {

                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = input.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesRead);
                }

                return fileName;
            } catch (IOException e) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "File upload failed", e.getMessage()));
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    private boolean isValidMimeType(String mimeType) {
        // Allow only common image MIME types
        return "image/jpeg".equals(mimeType) || "image/png".equals(mimeType) || "image/gif".equals(mimeType);
    }

    // Helper method to get the submitted filename
    private String getSubmittedFileName(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        if (contentDisp != null) {
            for (String cd : contentDisp.split(";")) {
                if (cd.trim().startsWith("filename")) {
                    System.out.println("Submitted filename: " + cd);
                    return cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
                }
            }
        }
        return "unknown";
    }

    // Getters and Setters
    public List<Product> getListProducts() {
        if (this.listProducts == null) {
            this.listProducts = productDaoImp.getAll();
        }
        return listProducts;
    }

    public List<Product> getFilteredProducts() {
        return filteredProducts;
    }

    public void setFilteredProducts(List<Product> filteredProducts) {
        this.filteredProducts = filteredProducts;
    }

    public Product getProduct() {
        return product;
    }

    public List<Category> getCategoryList() {
        if (this.categoryList == null) {
            loadCategories();
        }
        return categoryList;
    }

    public Long getSelectedCategoryId() {
        return selectedCategoryId;
    }

    public void setSelectedCategoryId(Long selectedCategoryId) {
        this.selectedCategoryId = selectedCategoryId;
    }

    public UploadedFile getImageFile() {
        return imageFile;
    }

    public void setImageFile(UploadedFile imageFile) {
        this.imageFile = imageFile;
        saveImageFile(imageFile);
    }

    public void deleteProduct(Long id) {
        try {
            Product productToDelete = productDaoImp.getById(id)
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            // Delete associated image first
            if (productToDelete.getImage() != null) {
                deleteOldImage(productToDelete.getImage());
            }

            // Delete the product
            productDaoImp.deleteById(id);

            // Refresh the list
            this.listProducts = productDaoImp.getAll();

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Product deleted successfully"));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error deleting product", e.getMessage()));
        }
    }
}