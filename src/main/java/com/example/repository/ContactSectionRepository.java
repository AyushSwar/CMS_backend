package com.example.repository;

import com.example.entity.ContactSection;
import com.example.entity.UserEntity;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

import java.util.Optional;

@Repository
public interface ContactSectionRepository extends CrudRepository<ContactSection, Long> {
     Optional<ContactSection> findByUser(UserEntity user);
}

