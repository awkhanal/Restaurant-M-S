package com.example.RestaurantMS.core.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface Service<T, I> {

    T save(T t, Optional<I> i);

    List<T> saveAll(List<T> entities);

    Optional<T> findOne(I i);

    List<T> findAll();

    List<T> findAllById(List<I> ids);

    void delete(T entity);

    void deleteById(I i);

    void deleteAllById(List<I> ids);

    Page<T> findPageableBySpec(Map<String, String> filterParams, Pageable pageable);

    List<T> findAllBySpec(Map<String, String> filterParams);

    Optional<T> findOneBySpec(Map<String, String> filterParams);
}


