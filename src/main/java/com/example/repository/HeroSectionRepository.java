package com.example.repository;

import com.example.entity.HeroSection;

import com.example.entity.UserEntity;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

import java.util.Optional;

@Repository
public interface HeroSectionRepository extends CrudRepository<HeroSection, Long> {
    Optional<HeroSection> findByUser(UserEntity user);
}
