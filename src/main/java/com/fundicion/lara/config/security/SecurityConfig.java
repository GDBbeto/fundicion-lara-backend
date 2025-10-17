package com.fundicion.lara.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable() // Desactiva CSRF (solo para pruebas locales)
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() // Permite todos los endpoints
                )
                .httpBasic().disable()
                .formLogin().disable();
        return http.build();
    }
}
