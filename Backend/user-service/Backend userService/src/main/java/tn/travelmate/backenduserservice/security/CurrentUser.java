package tn.travelmate.backenduserservice.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import tn.travelmate.backenduserservice.exception.ApiException;

/** Accès à l'utilisateur connecté (identifiant lu dans le jeton JWT). */
public final class CurrentUser {
    private CurrentUser() {
    }

    public static Long id() {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        if (a == null || !(a.getPrincipal() instanceof Long id)) {
            throw ApiException.unauthorized("Jeton absent ou invalide");
        }
        return id;
    }

    public static boolean isAdmin() {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        return a != null && a.getAuthorities().stream().anyMatch(x -> x.getAuthority().equals("ROLE_ADMINISTRATEUR"));
    }

    /** Autorise le propriétaire du compte ou un administrateur. */
    public static void requireOwnerOrAdmin(Long userId) {
        if (!isAdmin() && !id().equals(userId)) {
            throw ApiException.forbidden("Accès interdit à ce compte");
        }
    }

    /** Autorise uniquement le propriétaire du compte. */
    public static void requireOwner(Long userId) {
        if (!id().equals(userId)) {
            throw ApiException.forbidden("Cette action est réservée au propriétaire du compte");
        }
    }
}
