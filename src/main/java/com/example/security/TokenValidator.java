package com.example.security;

import com.example.exceptions.InvalidTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.inject.Singleton;

@Singleton
public class TokenValidator {

    public Claims validateToken(String token, String jwtSigningKey){
        try {
            Claims claims =  Jwts.parser()
                    .setSigningKey(jwtSigningKey)
                    .parseClaimsJws(token)
                    .getBody();// The token is valid, and claims can be extracted from it
            return claims;
        } catch (JwtException | IllegalArgumentException e) {
            // The token is invalid or expired
            System.out.println(token);
            throw new InvalidTokenException("Invalid or Expired Token.");
        }
    }
}
