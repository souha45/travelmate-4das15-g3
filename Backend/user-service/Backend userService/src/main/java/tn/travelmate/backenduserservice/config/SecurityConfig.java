package tn.travelmate.backenduserservice.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import tn.travelmate.backenduserservice.security.JwtAuthFilter;

import java.io.IOException;
import java.time.LocalDateTime;

@Configuration
public class SecurityConfig {

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http, JwtAuthFilter jwtFilter) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(a -> a
                        // public : inscription, connexion, documentation Swagger
                        .requestMatchers("/auth/**", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/error").permitAll()
                        // appels entre services (non exposés par la passerelle)
                        .requestMatchers("/internal/**").permitAll()
                        // administration
                        .requestMatchers("/admin/**").hasRole("ADMINISTRATEUR")
                        // le reste : jeton valide obligatoire (le propriétaire ou l'admin est vérifié dans les contrôleurs)
                        .anyRequest().authenticated())
                .exceptionHandling(e -> e
                        .authenticationEntryPoint((req, res, ex) -> ecrire(res, HttpStatus.UNAUTHORIZED, "Jeton absent ou invalide"))
                        .accessDeniedHandler((req, res, ex) -> ecrire(res, HttpStatus.FORBIDDEN, "Accès interdit")))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    /** Réponse d'erreur au format commun : status, error, message, timestamp. */
    private static void ecrire(HttpServletResponse res, HttpStatus s, String message) throws IOException {
        res.setStatus(s.value());
        res.setContentType("application/json;charset=UTF-8");
        res.getWriter().write("{\"status\":" + s.value() + ",\"error\":\"" + s.name()
                + "\",\"message\":\"" + message + "\",\"timestamp\":\"" + LocalDateTime.now() + "\"}");
    }
}
