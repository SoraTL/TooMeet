package com.toomeet.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RemoveSpecifiedHeader extends AbstractGatewayFilterFactory<RemoveSpecifiedHeader.Config> {

    public RemoveSpecifiedHeader() {
        super(Config.class);
    }

    private static HttpHeaders getHttpHeaders(ServerHttpRequest request) {
        HttpHeaders headers = request.getHeaders();

        List<String> nonAllowedHeaders = List.of(
                "x-user-id",
                "x-account-id",
                "x-user-email"
        );

        HttpHeaders modifiedHeaders = new HttpHeaders();
        headers.forEach((headerName, headerValues) -> {
            if (!nonAllowedHeaders.contains(headerName)) {
                modifiedHeaders.addAll(headerName, headerValues);
            }
        });
        return modifiedHeaders;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            HttpHeaders modifiedHeaders = getHttpHeaders(request);
            ServerHttpRequest modifiedRequest = new ServerHttpRequestDecorator(request) {
                @Override
                public HttpHeaders getHeaders() {
                    return modifiedHeaders;
                }
            };

            return chain.filter(exchange.mutate().request(modifiedRequest).build());

        });
    }

    public static class Config {

    }
}
