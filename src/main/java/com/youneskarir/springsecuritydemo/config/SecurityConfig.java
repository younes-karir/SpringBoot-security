package com.youneskarir.springsecuritydemo.config;


import jakarta.servlet.Filter;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.HandlerExceptionResolver;

import static com.youneskarir.springsecuritydemo.model.Permission.*;
import static com.youneskarir.springsecuritydemo.model.Role.ADMIN;
import static com.youneskarir.springsecuritydemo.model.Role.MANAGER;
import static org.springframework.http.HttpMethod.*;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {
    
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationProvider authenticationProvider;
    private final LogoutHandler logoutHandler;
    
    


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);
        http.authorizeHttpRequests(authorize -> {
            // authorizing all authentication endpoints    
            authorize.requestMatchers("/auth/**").permitAll();
            // authorized roles that can access management route
            authorize.requestMatchers("/api/v1/management").hasAnyRole(ADMIN.name(), MANAGER.name());
            // adding detailed permissions
            authorize.requestMatchers(GET,"api/v1/management").hasAnyAuthority(ADMIN_READ.name(),MANAGER_READ.name());
            authorize.requestMatchers(POST,"api/v1/management").hasAnyAuthority(ADMIN_CREATE.name(),MANAGER_CREATE.name());
            authorize.requestMatchers(PUT,"api/v1/management").hasAnyAuthority(ADMIN_UPDATE.name(),MANAGER_UPDATE.name());
            authorize.requestMatchers(DELETE,"api/v1/management").hasAnyAuthority(ADMIN_DELETE.name(),MANAGER_DELETE.name());
            // authorized roles that can access admin route
//            authorize.requestMatchers("/api/v1/admin").hasAnyRole(ADMIN.name());
//            // adding detailed permissions
//            authorize.requestMatchers(GET,"api/v1/admin").hasAnyAuthority(ADMIN_READ.name());
//            authorize.requestMatchers(POST,"api/v1/admin").hasAnyAuthority(ADMIN_CREATE.name());
//            authorize.requestMatchers(PUT,"api/v1/admin").hasAnyAuthority(ADMIN_UPDATE.name());
//            authorize.requestMatchers(DELETE,"api/v1/admin").hasAnyAuthority(ADMIN_DELETE.name());
            authorize.anyRequest().authenticated();
                });
               
        http.sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.authenticationProvider(authenticationProvider);
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        http.logout((logout) -> {
            logout.logoutUrl("/auth/logout");
            logout.addLogoutHandler(logoutHandler); // TODO : create the logout handler to handle the logic 
            logout.logoutSuccessHandler(((request, response, authentication) -> SecurityContextHolder.clearContext()));
            });
        return http.build();
    }

}
