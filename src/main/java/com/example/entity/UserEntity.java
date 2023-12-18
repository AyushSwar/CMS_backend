package com.example.entity;

import io.micronaut.core.annotation.Introspected;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Introspected
@Getter
@Setter
@Entity
@Table(name = "user_entity")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(name = "user_name")
    private String userName;

    private String email;

    private String password;

    // Establish a One-to-One relationship with HeroSection
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private HeroSection heroSection;
}
