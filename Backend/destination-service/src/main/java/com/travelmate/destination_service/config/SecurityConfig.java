package com.travelmate.destination_service.config;

import com.travelmate.destination_service.security.JwtAuthFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.time.LocalDateTime;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http, JwtAuthFilter jwtFilter) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(a -> a
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        // consultation publique
                        .requestMatchers(HttpMethod.GET, "/api/destinations/**", "/api/activites/**").permitAll()
                        .requestMatchers("/error").permitAll()
                        // actions du voyageur (jeton valide)
                        .requestMatchers(HttpMethod.POST, "/api/activites/*/avis").authenticated()
                        .requestMatchers("/api/avis/**", "/api/favoris/**").authenticated()
                        // le reste de /api/destinations et /api/activites : administrateur
                        .requestMatchers("/api/destinations/**", "/api/activites/**").hasRole("ADMINISTRATEUR")
                        .anyRequest().authenticated())
                .exceptionHandling(e -> e
                        .authenticationEntryPoint((req, res, ex) -> ecrire(res, HttpStatus.UNAUTHORIZED, "Jeton absent ou invalide"))
                        .accessDeniedHandler((req, res, ex) -> ecrire(res, HttpStatus.FORBIDDEN, "Accès interdit")))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    private static void ecrire(HttpServletResponse res, HttpStatus s, String message) throws IOException {
        res.setStatus(s.value());
        res.setContentType("application/json;charset=UTF-8");
        res.getWriter().write("{\"status\":" + s.value() + ",\"error\":\"" + s.name()
                + "\",\"message\":\"" + message + "\",\"timestamp\":\"" + LocalDateTime.now() + "\"}");
    }
}