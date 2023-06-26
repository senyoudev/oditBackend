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
import reactor.core.publisher.Mono;



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

            // Get the token from the request
            String token = request.getHeaders().getFirst("Authorization").substring(7);


            // Validate the token
            boolean isValidToken = validateToken(token);
            if (isValidToken) {
                return chain.filter(exchange);
            } else {
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE);
                DataBuffer buffer = response.bufferFactory().wrap("Unauthorized access to application".getBytes());
                return response.writeWith(Mono.just(buffer));
            }
        };
    }
    public Boolean validateToken(String token){
        try{
            InstanceInfo instance = discoveryClient.getNextServerFromEureka("AUTH", false);
            template.getForObject(instance.getHomePageUrl()+"/api/v1/auth/validate?token="+token, String.class);
            log.info(instance.getHomePageUrl());
            return true;
        }catch (Exception e){
            log.info(e.getMessage());
            return false;
        }
    }

    public static class Config {
    }

}