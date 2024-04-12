package com.toomeet.gateway.filter;

import com.toomeet.gateway.exceptions.UnauthorizedException;
import com.toomeet.gateway.jwt.JwtService;
import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AuthorizationFilter extends AbstractGatewayFilterFactory<AuthorizationFilter.Config> {


    @Autowired
    private JwtService jwtService;

    public AuthorizationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();


            List<String> headers = request.getHeaders().get(HttpHeaders.AUTHORIZATION);

            if (headers == null) {
                response.getHeaders().set("status", "Access Denied: Missing Authorization Header!");
                throw new UnauthorizedException("Missing Authorization Header!");
            }

            String authHeader = headers.get(0);

            if (StringUtils.isEmpty(authHeader) || !authHeader.startsWith("Bearer ")) {
                response.getHeaders().set("status", "Access Denied: Missing Authorization Header!");
                throw new UnauthorizedException("Missing Authorization Header!");
            }

            String token = authHeader.substring(7);


            if (jwtService.isTokenExpired(token)) {
                response.getHeaders().set("status", "Access Denied: Sorry, your token has expired!");
                throw new UnauthorizedException("Sorry, your token has expired!");
            }

            String userId = jwtService.extractUserId(token);
            String accountId = jwtService.extractAccountId(token);
            String userEmail = jwtService.extractUserEmail(token);

            exchange.getRequest().mutate().header("x-user-id", userId).build();
            exchange.getRequest().mutate().header("x-account-id", accountId).build();
            exchange.getRequest().mutate().header("x-user-email", userEmail).build();


            return chain.filter(exchange);
        });
    }

    public static class Config {
    }
}
