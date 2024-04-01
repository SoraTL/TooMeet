package com.toomeet.gateway.filter;

import com.toomeet.gateway.exceptions.UnauthorizedException;
import com.toomeet.gateway.jwt.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;

@Component
public class SocketAuthorizationFilter extends AbstractGatewayFilterFactory<SocketAuthorizationFilter.Config> {
    @Autowired
    private JwtService jwtService;

    public SocketAuthorizationFilter() {
        super(SocketAuthorizationFilter.Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {


        return ((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            HttpCookie cookie = request.getCookies().getFirst("access_token");
            if (cookie == null) {
                throw new UnauthorizedException("Missing Authorization Header!");
            }
            String token = cookie.getValue();

            if (jwtService.isTokenExpired(token)) {
                response.getHeaders().set("status", "Connect failed: Sorry, your token has expired!");
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
