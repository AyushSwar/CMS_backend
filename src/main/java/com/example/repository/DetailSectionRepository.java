package com.example.repository;

import com.example.entity.DetailSection;
import com.example.entity.UserEntity;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

import java.util.Optional;

@Repository
public interface DetailSectionRepository extends CrudRepository<DetailSection, Long> {
    Optional<DetailSection> findByUser(UserEntity user);
}
