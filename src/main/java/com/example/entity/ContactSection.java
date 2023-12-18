package com.example.entity;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.model.naming.NamingStrategies;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Introspected
@Getter
@Setter
@Entity
@MappedEntity(namingStrategy = NamingStrategies.LowerCase.class)
@Table(name = "contact_section", uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id"})})
public class ContactSection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT", name = "tile")
    private String tile;

    @Column(columnDefinition = "TEXT", name = "centered")
    private String centered;

    @Column(name = "active")
    private String active;

    // Establish the inverse side of the One-to-One relationship with UserEntity
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private UserEntity user;
}
