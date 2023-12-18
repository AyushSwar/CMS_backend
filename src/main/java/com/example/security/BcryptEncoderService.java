package com.example.security;

import jakarta.inject.Singleton;
import org.mindrot.jbcrypt.BCrypt;

@Singleton
public class BcryptEncoderService {
    public  String hashPassword(String plainTextPassword){
        return   BCrypt.hashpw(plainTextPassword, BCrypt.gensalt());
    }
    public boolean verifyPassword(String plainTextPassword, String hashedPassword){
        return BCrypt.checkpw(plainTextPassword, hashedPassword);
    }
}

//BCrypt hashing algorithm.