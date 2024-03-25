package com.toomeet.gateway.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtServiceImpl implements JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Override
    public String extractUserId(String token) {
        return extractClaims(token, (claims -> claims.get("sub").toString()));
    }

    @Override
    public String extractAccountId(String token) {
        return extractClaims(token, (claims -> claims.get("account_id").toString()));
    }

    @Override
    public String extractUserEmail(String token) {
        return extractClaims(token, (claims -> claims.get("email").toString()));
    }


    @Override
    public boolean isTokenExpired(String token) {
        return extractClaims(token, claims -> {
            long exp = Long.parseLong(claims.get("exp").toString());
            Instant instant = Instant.ofEpochSecond(exp);
            return Date.from(instant);
        }).before(new Date());

    }

    @Override
    public String generateToken(String userId) {
        return Jwts
                .builder()
                .setSubject(userId)
                .signWith(getSignKey())
                .setExpiration(new Date(System.currentTimeMillis() + 108000000))
                .compact();
    }

    private <T> T extractClaims(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }


    private Key getSignKey() {
        byte[] key = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(key);
    }
}
