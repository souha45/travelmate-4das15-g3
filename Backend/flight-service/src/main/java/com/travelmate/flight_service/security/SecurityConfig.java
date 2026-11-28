package com.travelmate.flight_service.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.springframework.http.HttpMethod.*;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private static final String ADMIN = "ADMINISTRATEUR";
    private static final String PARTENAIRE = "PARTENAIRE";
    private static final String VOYAGEUR = "VOYAGEUR";

    private final JwtService jwtService;

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)   // API stateless avec JWT : pas de session ni de cookie
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(e -> e
                        .authenticationEntryPoint((req, res, ex) -> ecrire(req, res, HttpStatus.UNAUTHORIZED,
                                "Authentification requise : jeton absent, invalide ou expiré"))
                        .accessDeniedHandler((req, res, ex) -> ecrire(req, res, HttpStatus.FORBIDDEN,
                                "Accès refusé : votre rôle ne permet pas cette action")))
                .authorizeHttpRequests(a -> a
                        // --- Public ---
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**", "/error").permitAll()
                        .requestMatchers(GET, "/api/vols/recherche").permitAll()
                        .requestMatchers(GET, "/api/aeroports/**").permitAll()

                        // --- Compagnies ---
                        .requestMatchers(GET, "/api/compagnies/moi").hasRole(PARTENAIRE)
                        .requestMatchers(GET, "/api/compagnies", "/api/compagnies/*").permitAll()
                        .requestMatchers(POST, "/api/compagnies").hasRole(PARTENAIRE)
                        .requestMatchers(PUT, "/api/compagnies/*/suspendre").hasRole(ADMIN)
                        .requestMatchers(PUT, "/api/compagnies/*", "/api/compagnies/*/reactiver").hasAnyRole(PARTENAIRE, ADMIN)
                        .requestMatchers(DELETE, "/api/compagnies/*").hasAnyRole(PARTENAIRE, ADMIN)

                        // --- Aéroports (données de référence gérées par l'admin) ---
                        .requestMatchers(POST, "/api/aeroports").hasRole(ADMIN)
                        .requestMatchers(PUT, "/api/aeroports/*").hasRole(ADMIN)
                        .requestMatchers(DELETE, "/api/aeroports/*").hasRole(ADMIN)

                        // --- Vols ---
                        .requestMatchers(POST, "/api/vols").hasRole(PARTENAIRE)
                        .requestMatchers(GET, "/api/vols").hasAnyRole(PARTENAIRE, ADMIN)
                        .requestMatchers(PUT, "/api/vols/*/approuver", "/api/vols/*/suspendre").hasRole(ADMIN)
                        .requestMatchers(PUT, "/api/vols/*", "/api/vols/*/retarder", "/api/vols/*/annuler").hasAnyRole(PARTENAIRE, ADMIN)
                        .requestMatchers(DELETE, "/api/vols/*").hasAnyRole(PARTENAIRE, ADMIN)

                        // --- Réservations ---
                        .requestMatchers(POST, "/api/reservations").hasRole(VOYAGEUR)
                        .requestMatchers(PUT, "/api/reservations/*/expirer").hasRole(ADMIN)

                        // Tout le reste (GET /api/vols/{id}, réservations...) : utilisateur connecté,
                        // la propriété est vérifiée dans les services
                        .anyRequest().authenticated())
                .addFilterBefore(new JwtAuthFilter(jwtService), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    /** Réponse JSON au même format que ErrorResponse. */
    private void ecrire(HttpServletRequest req, HttpServletResponse res, HttpStatus status, String message)
            throws IOException {
        res.setStatus(status.value());
        res.setContentType(MediaType.APPLICATION_JSON_VALUE);
        res.setCharacterEncoding("UTF-8");
        res.getWriter().write(String.format(
                "{\"timestamp\":\"%s\",\"status\":%d,\"erreur\":\"%s\",\"message\":\"%s\",\"chemin\":\"%s\",\"details\":null}",
                LocalDateTime.now(), status.value(), status.getReasonPhrase(), message, req.getRequestURI()));
    }
}