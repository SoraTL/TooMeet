package com.toomeet.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

@Component
public class RemoveSpecifiedHeader extends AbstractGatewayFilterFactory<RemoveSpecifiedHeader.Config> {

    public RemoveSpecifiedHeader() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            if (request.getHeaders().containsKey("x-user-id")) {
                ServerHttpRequest modifiedRequest = request.mutate()
                        .headers(httpHeaders -> httpHeaders.remove("x-user-id"))
                        .build();
                return chain.filter(exchange.mutate().request(modifiedRequest).build());
            }


            return chain.filter(exchange);
        });
    }

    public static class Config {

    }
}
