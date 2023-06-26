package com.oditbackend.apigw.filter;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Mono;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {



        @Autowired
    private RestTemplate template;

    public AuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
                if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                    throw new RuntimeException("missing authorization header");
                }

                String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    authHeader = authHeader.substring(7);
                }
                try {
                   template.getForObject("http://localhost:8080/validate?token=",authHeader[] , String.class);

                } catch (Exception e) {
                    return handleUnauthorized(exchange.getResponse(), "Unauthorized access to application");
                }

            return chain.filter(exchange);
        });
    }

    private Mono<Void> handleUnauthorized(ServerHttpResponse response, String errorMessage) {
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE);
        DataBuffer buffer = response.bufferFactory().wrap(errorMessage.getBytes());
        return response.writeWith(Mono.just(buffer));
    }

    public static class Config {

    }
}