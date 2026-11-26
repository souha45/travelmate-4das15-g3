package tn.travelmate.backenduserservice.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/** Lit l'en-tête Authorization: Bearer <jeton> et identifie l'utilisateur pour la requête. */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtService jwt;

    public JwtAuthFilter(JwtService jwt) {
        this.jwt = jwt;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {
        String h = req.getHeader("Authorization");
        if (h != null && h.startsWith("Bearer ")) {
            jwt.lire(h.substring(7).trim()).ifPresent(this::authentifier);
        }
        chain.doFilter(req, res);
    }

    private void authentifier(Claims c) {
        List<GrantedAuthority> autorites = new ArrayList<>();
        Object roles = c.get("roles");
        if (roles instanceof List<?> liste) {
            for (Object r : liste) autorites.add(new SimpleGrantedAuthority("ROLE_" + r));
        }
        var auth = new UsernamePasswordAuthenticationToken(Long.valueOf(c.getSubject()), null, autorites);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}
