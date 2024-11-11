package org.example.ecom.Service;


import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import java.util.List;
import java.util.Optional;
import org.example.ecom.Dao.Dao;
import org.example.ecom.Util.EntityManagerProvider;
import org.example.ecom.model.Category;

public class CategoryDaoImp implements Dao<Category> {
  public CategoryDaoImp() {
  }

  @Override
  public Optional<Category> getById(Long id) {
    if (id == null) {
      return Optional.empty();
    }
    EntityManager entityManager = EntityManagerProvider.getEntityManager();
    try {
      entityManager.getTransaction().begin();
      Category category = entityManager.find(Category.class, id);
      entityManager.getTransaction().commit();
      return Optional.ofNullable(category);
    } catch (Exception e) {
      if (entityManager.getTransaction().isActive()) {
        entityManager.getTransaction().rollback();
      }
      return Optional.empty();
    } finally {
      entityManager.close();
    }
  }

  @Override
  public List<Category> getAll() {
    EntityManager entityManager = EntityManagerProvider.getEntityManager();
    try {
      entityManager.getTransaction().begin();
      List<Category> categories = entityManager.createQuery("SELECT c FROM Category c", Category.class).getResultList();
      entityManager.getTransaction().commit();
      return categories;
    } catch (Exception e) {
      if (entityManager.getTransaction().isActive()) {
        entityManager.getTransaction().rollback();
      }
      return List.of();
    } finally {
      entityManager.close();
    }
  }

  @Override
  public Optional<Category> update(Category obj) {
    if (obj == null || obj.getId() == null) {
      return Optional.empty();
    }
    EntityManager entityManager = EntityManagerProvider.getEntityManager();
    try {
      entityManager.getTransaction().begin();
      Category category = entityManager.find(Category.class, obj.getId());
      if (category != null) {
        category.setName(obj.getName());
        category.setDescription(obj.getDescription());
        entityManager.merge(category);
        entityManager.getTransaction().commit();
        return Optional.of(category);
      }
      entityManager.getTransaction().commit();
      return Optional.empty();
    } catch (Exception e) {
      if (entityManager.getTransaction().isActive()) {
        entityManager.getTransaction().rollback();
      }
      return Optional.empty();
    } finally {
      entityManager.close();
    }
  }

  @Override
  public Category create(Category obj) {
    if (obj == null) {
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
      Category category = entityManager.find(Category.class, id);
      if (category != null) {
        entityManager.remove(category);
        entityManager.getTransaction().commit();
        return 1;
      }
      entityManager.getTransaction().commit();
      return 0;
    } catch (Exception e) {
      if (entityManager.getTransaction().isActive()) {
        entityManager.getTransaction().rollback();
      }
      return 0;
    } finally {
      entityManager.close();
    }
  }

    public List<Category> getCategoryByKeyWord(String keyWord) {
        EntityManager entityManager = EntityManagerProvider.getEntityManager();
        try {
            entityManager.getTransaction().begin();
            List<Category> categories = entityManager.createQuery("SELECT c FROM Category c WHERE c.name LIKE :keyWord or c.description LIKE :keyWord", Category.class)
                    .setParameter("keyWord", "%" + keyWord + "%")
                    .getResultList();
            entityManager.getTransaction().commit();
            return categories;
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            return List.of();
        } finally {
            entityManager.close();
        }
    }
}
