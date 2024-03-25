package com.toomeet.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;

public class RateLimitingFilter extends AbstractGatewayFilterFactory<RateLimitingFilter.config> {

    @Override
    public GatewayFilter apply(config config) {
        return ((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();


            return chain.filter(exchange);
        });
    }

    public static class config {
    }
}
