package org.example.ecom.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.example.ecom.Dao.Dao;
import org.example.ecom.Util.EntityManagerProvider;
import org.example.ecom.model.Category;
import org.example.ecom.model.Product;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ProductDaoImpl implements Dao<Product> {
  public ProductDaoImpl() {}

  @Override
  public Optional<Product> getById(Long id) {
    if (id == null) {
      return Optional.empty();
    }

    EntityManager entityManager = EntityManagerProvider.getEntityManager();

    Product product = null;
    try {
      entityManager.getTransaction().begin();
      product = entityManager.find(Product.class, id);
      entityManager.getTransaction().commit();
    } catch (PersistenceException e) {
      if (entityManager.getTransaction().isActive()) {
        entityManager.getTransaction().rollback();
      }
      throw new RuntimeException("Failed to fetch product by id: " + e.getMessage(), e);
    } finally {
      entityManager.close();
    }

    return Optional.ofNullable(product);
  }

  @Transactional
  public Map<Category, Long> countProductsByCategory() {
    EntityManager entityManager = EntityManagerProvider.getEntityManager();
    Map<Category, Long> categoryCounts = new HashMap<>();

    // JPQL query to count products by category
    String jpql = "SELECT p.category, COUNT(p) FROM Product p GROUP BY p.category";
    TypedQuery<Object[]> query = entityManager.createQuery(jpql, Object[].class);

    // Execute the query and get the result list
    List<Object[]> resultList = query.getResultList();

    // Process the result list and populate the categoryCounts map
    for (Object[] result : resultList) {
      Category category = (Category) result[0];
      Long count = (Long) result[1];
      categoryCounts.put(category, count);
    }

    return categoryCounts;
  }

  public List<Product> getProductByCategory(Long categoryId) {
    EntityManager entityManager = EntityManagerProvider.getEntityManager();
    List<Product> products = null;
    try {
      entityManager.getTransaction().begin();
      products =
              entityManager
                      .createQuery(
                              "SELECT p FROM Product p WHERE p.category.id = :categoryId",
                              Product.class)
                      .setParameter("categoryId", categoryId)
                      .getResultList();

      entityManager.getTransaction().commit();
    } catch (PersistenceException e) {
      if (entityManager.getTransaction().isActive()) {
        entityManager.getTransaction().rollback();
      }
      throw new RuntimeException("Failed to fetch products: " + e.getMessage(), e);
    } finally {
      entityManager.close();
    }

    return products;
  }

  public List<Product> getProductByKeyWord(String keyWord) {
    EntityManager entityManager = EntityManagerProvider.getEntityManager();
    List<Product> products = null;
    try {
      entityManager.getTransaction().begin();

      products =
          entityManager
              .createQuery(
                  "SELECT p FROM Product p WHERE p.name LIKE :keyword OR p.description LIKE :keyword",
                  Product.class)
              .setParameter("keyword", "%" + keyWord+ "%")
              .getResultList();

      entityManager.getTransaction().commit();
    } catch (PersistenceException e) {
      if (entityManager.getTransaction().isActive()) {
        entityManager.getTransaction().rollback();
      }
      throw new RuntimeException("Failed to fetch products: " + e.getMessage(), e);
    } finally {
      entityManager.close();
    }

    return products;
  }

  @Override
  public List<Product> getAll() {
    EntityManager entityManager = EntityManagerProvider.getEntityManager();

    List<Product> products = null;
    try {
      entityManager.getTransaction().begin();
      products =
          entityManager.createQuery("SELECT p FROM Product p", Product.class).getResultList();
      entityManager.getTransaction().commit();
    } catch (PersistenceException e) {
      if (entityManager.getTransaction().isActive()) {
        entityManager.getTransaction().rollback();
      }
      throw new RuntimeException("Failed to fetch products: " + e.getMessage(), e);
    } finally {
      entityManager.close();
    }

    return products;
  }

  @Override
  public Optional<Product> update(Product obj) {
    if (obj == null || obj.getId() == null) {
      return Optional.empty();
    }
    EntityManager entityManager = EntityManagerProvider.getEntityManager();
    try {
      entityManager.getTransaction().begin();
      Optional<Product> productOptional = getById(obj.getId());
      if (productOptional.isPresent()) {
        Product existingProduct = productOptional.get();
        existingProduct.setCategory(obj.getCategory());
        existingProduct.setDescription(obj.getDescription());
        existingProduct.setName(obj.getName());
        existingProduct.setSdr(obj.getSdr());
        existingProduct.setPrice(obj.getPrice());
        existingProduct.setQuantity(obj.getQuantity());
        existingProduct.setImage(obj.getImage());

        entityManager.merge(existingProduct);

        entityManager.getTransaction().commit();
        return Optional.of(existingProduct);
      } else {
        return Optional.empty();
      }
    } catch (PersistenceException e) {
      if (entityManager.getTransaction().isActive()) {
        entityManager.getTransaction().rollback();
      }
      throw new RuntimeException("Failed to update product: " + e.getMessage(), e);
    } finally {
      entityManager.close();
    }
  }

  @Override
  public Product create(Product obj) {
    if (obj == null) {
      System.out.println("Product cannot be null.");
      return null;
    }

    EntityManager entityManager = EntityManagerProvider.getEntityManager();
    try {
      entityManager.getTransaction().begin();
      entityManager.persist(obj);
      entityManager.getTransaction().commit();
      return obj;
    } catch (PersistenceException e) {
      if (entityManager.getTransaction().isActive()) {
        entityManager.getTransaction().rollback();
      }
      System.out.println("Failed to create product: " + e.getMessage());
      return null;
    } finally {
      entityManager.close();
    }
  }

  @Override
  public int deleteById(Long id) {
    if (id == null) {
      return 0;
    }

    EntityManager entityManager = EntityManagerProvider.getEntityManager();
    try {
      entityManager.getTransaction().begin();
      Product product = entityManager.find(Product.class, id);
      if (product != null) {
        entityManager.remove(product);
        entityManager.getTransaction().commit();
        return 1;
      } else {
        return 0;
      }
    } catch (PersistenceException e) {
      if (entityManager.getTransaction().isActive()) {
        entityManager.getTransaction().rollback();
      }
      System.out.println("Failed to delete product: " + e.getMessage());
      return 0;
    } finally {
      entityManager.close();
    }
  }
}
