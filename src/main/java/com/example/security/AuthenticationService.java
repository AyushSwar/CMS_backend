package com.example.security;

import com.example.LoginRequest;
import com.example.entity.UserEntity;
import com.example.repository.UserRepository;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.util.Optional;

@Singleton
public class AuthenticationService {

    @Inject
    private BcryptEncoderService bcryptEncoderService;

    @Inject
    private UserRepository userRepository;

    // Return UserEntity on successful authentication, null otherwise
    public UserEntity authenticateUser(LoginRequest request) {
        try {
            Optional<UserEntity> userOpt = userRepository.findByUserName(request.getUserName());

            if (userOpt.isPresent()) {
                UserEntity user = userOpt.get();

                // Verify the password
                if (bcryptEncoderService.verifyPassword(request.getPassword(), user.getPassword())) {
                    // Authentication successful, return the user entity
                    return user;
                } else {
                    // Password verification failed
                    return null;
                }
            } else {
                // User not found
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
