package com.oditbackend.apigw.filter;


import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.URISyntaxException;


@Component
@Slf4j
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    @Autowired
    private RestTemplate template;

    @Autowired
    private EurekaClient discoveryClient;
    public AuthenticationFilter(RestTemplate restTemplate) {
        super(Config.class);
    }
    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();
            String requestPath = request.getPath().value();

            // Get the token from the request
            String token = request.getHeaders().getFirst("Authorization").substring(7);

            if (requestPath.startsWith("/api/v1/admin/")) {

                if (!isTokenValidAsAdmin(token)) {
                    response.setStatusCode(HttpStatus.UNAUTHORIZED);
                    response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE);
                    return response.writeWith(Mono.just(response.bufferFactory().wrap("Unauthorized access to admin resource".getBytes())));
                }
            }
            // Validate the token
            TokenValidationResponse tokenResponse = validateToken(token);
            Integer userId = tokenResponse.getUserId();
            String username = tokenResponse.getUsername();
            if (userId != -1) {
                URI uri = null;
                try {
//                    uri = new URI(request.getURI()+"?userId="+userId);
                    String existingUri = request.getURI().toString();
                    String parameterSeparator = existingUri.contains("?") ? "&" : "?";
                    uri = new URI(existingUri + parameterSeparator + "userId=" + userId + "&username="+username);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
                ServerHttpRequest modifiedRequest = request.mutate().uri(uri).build();
                ServerWebExchange modifiedExchange = exchange.mutate().request(modifiedRequest).build();
                return chain.filter(modifiedExchange);
            } else {
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE);
                DataBuffer buffer = response.bufferFactory().wrap("Unauthorized access to application".getBytes());
                return response.writeWith(Mono.just(buffer));
            }
        };
    }
    public TokenValidationResponse validateToken(String token){
        try{
            InstanceInfo instance = discoveryClient.getNextServerFromEureka("AUTH", false);
            TokenValidationResponse res = template.getForObject(instance.getHomePageUrl()+"/api/v1/auth/validate?token="+token, TokenValidationResponse.class);
            return res;
        }catch (Exception e){
            log.info(e.getMessage());
            return null;
        }
    }

    public Boolean isTokenValidAsAdmin(String token){
        try{
            InstanceInfo instance = discoveryClient.getNextServerFromEureka("AUTH", false);
            Boolean res = template.getForObject(instance.getHomePageUrl()+"/api/v1/auth/validate-admin?token="+token, Boolean.class);
            return res;
        }catch (Exception e){
            log.info(e.getMessage());
            return false;
        }
    }

    public static class Config {
    }

}