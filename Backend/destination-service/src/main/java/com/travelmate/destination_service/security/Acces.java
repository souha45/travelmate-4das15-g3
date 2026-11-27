package com.travelmate.destination_service.security;

import com.travelmate.destination_service.exception.ApiException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

public final class Acces {
    private Acces() {}

    public static Utilisateur connecte() {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        if (a == null || !a.isAuthenticated() || a instanceof AnonymousAuthenticationToken) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Authentification requise.");
        }
        List<String> roles = a.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(r -> r.startsWith("ROLE_") ? r.substring(5) : r)
                .toList();
        return new Utilisateur(a.getName(), roles);
    }

    public static Utilisateur admin() {
        Utilisateur u = connecte();
        if (!u.estAdmin()) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Accès réservé à l'administrateur.");
        }
        return u;
    }

    // Anciennes signatures conservées : les contrôleurs n'ont rien à changer.
    public static Utilisateur connecte(HttpServletRequest requete) { return connecte(); }
    public static Utilisateur admin(HttpServletRequest requete) { return admin(); }
}