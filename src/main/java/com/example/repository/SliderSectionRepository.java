package com.example.repository;

import com.example.entity.SliderSection;
import com.example.entity.UserEntity;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

import java.util.Optional;

@Repository
public interface SliderSectionRepository extends CrudRepository<SliderSection, Long> {
    Optional<SliderSection> findByUser(UserEntity user);
}
