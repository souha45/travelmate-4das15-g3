package com.travelmate.flight_service.security;

import com.travelmate.flight_service.exception.ForbiddenException;
import com.travelmate.flight_service.exception.UnauthorizedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class CurrentUser {

    private CurrentUser() {}

    private static Authentication auth() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /** Id de l'utilisateur, ou null s'il est anonyme (pour les endpoints publics). */
    public static Long idOrNull() {
        Authentication a = auth();
        return (a != null && a.getPrincipal() instanceof Long id) ? id : null;
    }

    public static Long id() {
        Long id = idOrNull();
        if (id == null) throw new UnauthorizedException("Authentification requise");
        return id;
    }

    private static boolean aAutorite(String autorite) {
        Authentication a = auth();
        return a != null && a.getAuthorities().stream().anyMatch(x -> x.getAuthority().equals(autorite));
    }

    public static boolean isAdmin()      { return aAutorite("ROLE_ADMINISTRATEUR"); }
    public static boolean isPartenaire() { return aAutorite("ROLE_PARTENAIRE"); }
    public static boolean isVoyageur()   { return aAutorite("ROLE_VOYAGEUR"); }

    /** Autorise le propriétaire (partenaire de la compagnie, ou voyageur de la réservation) ou un admin. */
    public static void exigerProprietaireOuAdmin(Long proprietaireId) {
        if (!isAdmin() && !id().equals(proprietaireId)) {
            throw new ForbiddenException("Cette ressource ne vous appartient pas");
        }
    }

    /**
     * Un partenaire HOTEL ou LOUEUR_VOITURE ne doit pas créer de compagnie aérienne.
     * Si le token ne contient pas le type (user-service pas encore modifié), on n'empêche rien.
     */
    public static void exigerTypeCompagnieAerienne() {
        Authentication a = auth();
        boolean typeConnu = a != null && a.getAuthorities().stream()
                .anyMatch(x -> x.getAuthority().startsWith("TYPE_"));
        if (typeConnu && !aAutorite("TYPE_COMPAGNIE_AERIENNE")) {
            throw new ForbiddenException("Seul un partenaire de type COMPAGNIE_AERIENNE peut gérer des compagnies et des vols");
        }
    }
}