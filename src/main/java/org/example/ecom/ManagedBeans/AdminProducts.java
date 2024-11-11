package org.example.ecom.ManagedBeans;

import org.example.ecom.Service.CategoryDaoImp;
import org.example.ecom.Service.ProductDaoImpl;
import org.example.ecom.model.Category;
import org.example.ecom.model.Product;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

@ManagedBean(name = "adminProducts")
@ViewScoped
public class AdminProducts implements Serializable {
    private final ProductDaoImpl productDaoImp = new ProductDaoImpl();
    private final CategoryDaoImp categoryDaoImp = new CategoryDaoImp();

    private List<Product> listProducts;
    private List<Product> filteredProducts;
    private List<Category> categoryList;
    private Product product = new Product();
    private Long selectedCategoryId;

    public AdminProducts() {
        loadCategories();
    }

    private void loadCategories() {
        this.categoryList = categoryDaoImp.getAll();
    }

    public void prepareNewProduct() {
        this.product = new Product();
        this.selectedCategoryId = null;
    }

    public void setProduct(Product product) {
        this.product = new Product();
        this.product.setId(product.getId());
        this.product.setName(product.getName());
        this.product.setDescription(product.getDescription());
        this.product.setPrice(product.getPrice());
        this.product.setQuantity(product.getQuantity());
        this.product.setCategory(product.getCategory());
        this.selectedCategoryId = product.getCategory().getId();
    }

    public void saveProduct() {
        try {
            if (selectedCategoryId != null) {
                Category selectedCategory = categoryDaoImp.getById(selectedCategoryId).get();
                product.setCategory(selectedCategory);
            }

            if (product.getId() != null) {
                productDaoImp.update(product);
            } else {
                productDaoImp.create(product);
            }

            this.listProducts = productDaoImp.getAll();
            this.product = new Product();
            this.selectedCategoryId = null;
        } catch (Exception e) {
            // Handle exception appropriately
            e.printStackTrace();
        }
    }

    public void deleteProduct(Long id) {
        productDaoImp.deleteById(id);
        getListProducts();
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
}