package com.travelmate.flight_service.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/** Lit "Authorization: Bearer <jeton>" et identifie l'utilisateur pour la requête. */
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwt;

    public JwtAuthFilter(JwtService jwt) {
        this.jwt = jwt;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {
        String header = req.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            jwt.lire(header.substring(7).trim()).ifPresent(this::authentifier);
        }
        chain.doFilter(req, res);
    }

    private void authentifier(Claims c) {
        try {
            List<GrantedAuthority> autorites = new ArrayList<>();
            if (c.get("roles") instanceof List<?> roles) {
                for (Object r : roles) autorites.add(new SimpleGrantedAuthority("ROLE_" + r));
            }
            // Type de partenaire (COMPAGNIE_AERIENNE, HOTEL...) si présent dans le token
            if (c.get("typePartenaire") instanceof String type) {
                autorites.add(new SimpleGrantedAuthority("TYPE_" + type));
            }
            var auth = new UsernamePasswordAuthenticationToken(Long.valueOf(c.getSubject()), null, autorites);
            SecurityContextHolder.getContext().setAuthentication(auth);
        } catch (NumberFormatException e) {
            // sub invalide : on laisse la requête anonyme (401 si l'endpoint est protégé)
        }
    }
}