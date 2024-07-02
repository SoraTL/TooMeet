package com.toomet.chat.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtService {
    @Value("${jwt.secret_key}")
    private String secretKey;

    @Value("${jwt.expired_time}")
    private Long expiredTime;

    public Long getTokenExpiredTime() {
        return expiredTime;
    }

    public String generateJoinToken(Long roomId) {
        Date now = new Date(System.currentTimeMillis());
        Date expiredTime = new Date(System.currentTimeMillis() + this.expiredTime);

        Claims claims = Jwts.claims();
        String subject = roomId.toString();
        claims.put("sub", subject);
        claims.put("room_id", roomId);
        claims.put("iat", now.getTime());
        claims.put("exp", expiredTime);
        claims.put("iss", "minnhieuano-toomeet");

        return Jwts
                .builder()
                .setClaims(claims)
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Long extractJoinRoomToken(String token) {
        return extractClaims(token, claims -> Long.parseLong(claims.get("room_id").toString()));
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private <T> T extractClaims(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }


    public boolean isTokenExpired(String token) {
        return extractClaims(token, claims -> {
            long exp = Long.parseLong(claims.get("exp").toString());
            Instant instant = Instant.ofEpochSecond(exp);
            return Date.from(instant);
        }).before(new Date());

    }

    private Key getSignInKey() {
        byte[] key = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(key);
    }


}
