package com.oditbackend.apigw.filter;


import com.example.helpers.exceptions.UnauthorizedException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ServerWebExchange;

import java.net.URI;
import java.net.URISyntaxException;


@Component
@Slf4j
@AllArgsConstructor
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    private final RestTemplate restTemplate;


    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();
            String requestPath = request.getPath().value();

            // Get the token from the request
            String authorization = request.getHeaders().getFirst("Authorization");

            if (authorization != null) {
                String token = authorization.substring(7);

                if (requestPath.startsWith("/api/v1/admin/") && isTokenValidAsAdmin(token)) {
                    return chain.filter(exchange);
                }

                // Validate the token
                TokenValidationResponse tokenResponse = validateToken(token);
                Integer userId = tokenResponse.getUserId();
                String username = tokenResponse.getUsername();

                URI uri = null;
                try {
                    String existingUri = request.getURI().toString();
                    String parameterSeparator = existingUri.contains("?") ? "&" : "?";
                    uri = new URI(existingUri + parameterSeparator + "userId=" + userId + "&username=" + username);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
                ServerHttpRequest modifiedRequest = request.mutate().uri(uri).build();
                ServerWebExchange modifiedExchange = exchange.mutate().request(modifiedRequest).build();
                return chain.filter(modifiedExchange);
            } else {
                throw new UnauthorizedException("Unauthorized access to application");
            }
        };
    }

    public TokenValidationResponse validateToken(String token) {
        try {
            TokenValidationResponse res = restTemplate.getForObject("http://auth/api/v1/auth/validate?token=" + token, TokenValidationResponse.class);
            return res;
        } catch (Exception e) {
            log.info(e.getMessage());
            throw new UnauthorizedException("Unauthorized access to application");
        }
    }

    public Boolean isTokenValidAsAdmin(String token) {
        try {
            //InstanceInfo instance = discoveryClient.getNextServerFromEureka("AUTH", false);
            Boolean res = restTemplate.getForObject("http://auth/api/v1/auth/validate-admin?token=" + token, Boolean.class);
            return res;
        } catch (Exception e) {
            throw new UnauthorizedException("Unauthorized access to admin resource");
        }
    }

    public static class Config {
    }

}