package com.example.repository;

import com.example.entity.NavSection;
import com.example.entity.UserEntity;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

import java.util.Optional;

@Repository
public interface NavSectionRepository extends CrudRepository<NavSection, Long> {
    Optional<NavSection> findByUser(UserEntity user);
}
