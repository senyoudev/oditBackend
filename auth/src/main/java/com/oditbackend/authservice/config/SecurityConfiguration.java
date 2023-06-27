package com.oditbackend.authservice.config;

import com.oditbackend.authservice.entity.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.http.HttpMethod.*;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final AuthenticationProvider authenticationProvider;
    public static final String[] whiteListedRoutes = new String[]{"/api/v1/auth/**","/api/v1/users/**"};

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf->csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(whiteListedRoutes).permitAll()
                        .requestMatchers(GET,"/api/v1/admin/**")
                        .hasAnyAuthority(Role.Admin.name())
                        .requestMatchers(POST,"/api/v1/admin/**")
                        .hasAnyAuthority(Role.Admin.name())
                        .requestMatchers(PUT,"/api/v1/admin/**")
                        .hasAnyAuthority(Role.Admin.name())
                        .requestMatchers(DELETE,"/api/v1/admin/**")
                        .hasAnyAuthority(Role.Admin.name())
                        .anyRequest().authenticated()
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider);
        return http.build();
    }
}