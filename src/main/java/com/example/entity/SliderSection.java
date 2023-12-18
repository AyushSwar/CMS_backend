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
@Table(name = "Slider_section", uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id"})})
public class SliderSection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT", name = "basic")
    private String basic;

    // Establish the inverse side of the One-to-One relationship with UserEntity
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private UserEntity user;
}
