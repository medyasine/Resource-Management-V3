package org.example.ecom.Dao;

import java.util.List;
import java.util.Optional;

public interface Dao<T> {
    Optional<T> getById(Long id);
    List<T> getAll();
    Optional<T> update(T obj);
    T create(T obj);
    int deleteById(Long id);
}
