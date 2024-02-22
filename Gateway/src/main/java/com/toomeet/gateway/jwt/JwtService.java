package com.toomeet.gateway.jwt;

public interface JwtService {
    String extractUserId(String token);

    boolean isTokenExpired(String token);

    String generateToken(String userId);
}
