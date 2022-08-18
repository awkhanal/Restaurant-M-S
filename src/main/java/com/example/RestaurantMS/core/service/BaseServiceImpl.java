package com.example.RestaurantMS.core.service;


import com.example.RestaurantMS.core.repository.BaseRepository;
import com.example.RestaurantMS.core.repository.BaseSpecBuilder;
import com.example.RestaurantMS.core.utils.MyBeansUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
public abstract class BaseServiceImpl<T, I> implements Service<T, I> {

    private final BaseRepository<T, I> repository;

    protected BaseServiceImpl(
            BaseRepository<T, I> repository) {
        this.repository = repository;
    }

    @Override
    public List<T> findAll() {
        return repository.findAll();
    }

    @Override
    public void delete(T entity) {
        repository.delete(entity);
    }

    @Override
    public void deleteById(I i) {
        Optional<T> ll = (Optional<T>) findOne(i).get();
        Assert.isTrue(ll.isPresent(), "error: entity to delete not found for id " + i);
        repository.deleteById(i);
    }

    @Override
    public Page<T> findPageableBySpec(Map<String, String> filterParams, Pageable pageable) {
        final BaseSpecBuilder<T> builder = getSpec(filterParams);
        final Specification<T> spec = builder.build();

        return repository.findAll(spec, pageable);
    }

    @Override
    public List<T> findAllBySpec(Map<String, String> filterParams) {
        final BaseSpecBuilder<T> builder = getSpec(filterParams);
        final Specification<T> spec = builder.build();

        return repository.findAll(spec);
    }

    @Override
    public Optional<T> findOneBySpec(Map<String, String> filterParams) {
        final BaseSpecBuilder<T> builder = getSpec(filterParams);
        final Specification<T> spec = builder.build();

        return repository.findOne(spec);
    }

    @Override
    public Optional<T> findOne(I i) {
        return repository.findById(i);
    }

    @Override
    public List<T> findAllById(List<I> ids) {
        return repository.findAllById(ids);
    }

    @Override
    public T save(T t, Optional<I> id) {
        if (id.isPresent()) {
            I i = id.get();
            log.info("id------{}", i);
            Optional<T> existingEntity = repository.findById(i);
            Assert.isTrue(existingEntity.isPresent(), "Entity does not exist for id "+ i);
            MyBeansUtil<T> myBeansUtil = new MyBeansUtil<>();
            myBeansUtil.copyNonNullProperties(existingEntity.get(),t);
            t = existingEntity.get();
        }
        return repository.save(t);
    }

    @Override
    public List<T> saveAll(List<T> list) {
        return repository.saveAll(list);
    }

    protected abstract BaseSpecBuilder<T> getSpec(Map<String, String> filterParams);

}

