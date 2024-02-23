package com.toomeet.gateway.jwt;

public interface JwtService {
    String extractUserId(String token);

    String extractUserEmail(String token);

    String extractAccountId(String token);

    boolean isTokenExpired(String token);

    String generateToken(String userId);
}
