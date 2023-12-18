package com.example.security;

import com.example.LoginRequest;
import jakarta.inject.Singleton;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Singleton
public class TokenHandler {
    public String generateAccessToken(LoginRequest request) {
        return Jwts.builder()
                .setSubject(request.getUserName())
                .claim("userName", request.getUserName())
//                from Constant.java for signing the key.
                .signWith(SignatureAlgorithm.HS256, Constants.JWT_SIGNING_KEY)
                .compact();


    }
}
