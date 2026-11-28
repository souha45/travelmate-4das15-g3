package com.travelmate.flight_service.security;

import com.travelmate.flight_service.exception.ForbiddenException;
import com.travelmate.flight_service.exception.UnauthorizedException;

/** Identité de l'appelant, fournie par l'API Gateway (en-têtes X-User-Id / X-User-Role). */
public record UtilisateurCourant(Long id, RoleUtilisateur role) {

    public static UtilisateurCourant de(Long id, String role) {
        if (role == null || role.isBlank()) {
            return new UtilisateurCourant(null, RoleUtilisateur.VISITEUR);   // appel anonyme
        }
        RoleUtilisateur r;
        try {
            r = RoleUtilisateur.valueOf(role.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new UnauthorizedException("Rôle inconnu : " + role);
        }
        if (r != RoleUtilisateur.VISITEUR && id == null) {
            throw new UnauthorizedException("En-tête X-User-Id manquant");
        }
        return new UtilisateurCourant(id, r);
    }

    public boolean estAdmin()      { return role == RoleUtilisateur.ADMINISTRATEUR; }
    public boolean estPartenaire() { return role == RoleUtilisateur.PARTENAIRE; }

    public void exigerAuthentifie() {
        if (role == RoleUtilisateur.VISITEUR) {
            throw new UnauthorizedException("Authentification requise");
        }
    }

    public void exigerAdmin() {
        exigerAuthentifie();
        if (!estAdmin()) throw new ForbiddenException("Action réservée à l'administrateur");
    }

    public void exigerPartenaireOuAdmin() {
        exigerAuthentifie();
        if (!estAdmin() && !estPartenaire()) {
            throw new ForbiddenException("Action réservée aux partenaires et à l'administrateur");
        }
    }
}