package com.example.repository;

import com.example.entity.FooterSection;
import com.example.entity.UserEntity;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

import java.util.Optional;

@Repository
public interface FooterSectionRepository extends CrudRepository<FooterSection, Long> {
    Optional<FooterSection> findByUser(UserEntity user);
}
