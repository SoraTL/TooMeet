package com.toomeet.user.jwt;

import com.google.gson.Gson;
import com.toomeet.user.auth.Account;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    private final Gson gson;
    private final ModelMapper mapper;

    @Value("${jwt.secret_key}")
    private String secretKey;

    @Value("${jwt.expired_time}")
    private Long expiredTime;

    @Override
    public Long getTokenExpiredTime() {
        return expiredTime;
    }

    @Override
    public String generateToken(Account account) {

        Date now = new Date(System.currentTimeMillis());
        Date expiredTime = new Date(System.currentTimeMillis() + this.expiredTime);

        Claims claims = Jwts.claims();

        String subject = account.getUser().getId().toString();

        claims.put("account_id", account.getId());
        claims.put("email", account.getEmail());
        claims.put("user_id", subject);
        claims.put("sub", subject);
        claims.put("iat", now.getTime());
        claims.put("exp", expiredTime);
        claims.put("iss", "toomeet-toomeet-toomeet");


        return Jwts
                .builder()
                .setClaims(claims)
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }


    private <T> T extractClaims(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] key = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(key);
    }

    private boolean isTokenExpired(String token) {
        return extractClaims(token, Claims::getExpiration).before(new Date());
    }

}
