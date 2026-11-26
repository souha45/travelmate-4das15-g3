package tn.travelmate.backenduserservice.dto;

import jakarta.validation.constraints.*;
import tn.travelmate.backenduserservice.ennum.NomRole;
import tn.travelmate.backenduserservice.ennum.StatutCompte;
import tn.travelmate.backenduserservice.ennum.TypePartenaire;
import tn.travelmate.backenduserservice.ennum.TypeVoyage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Tous les objets échangés par l'API (requêtes et réponses).
 */
public final class Dtos {
    private Dtos() {
    }

    // ---------- requêtes
    /**
     * Inscription commune à tous les utilisateurs.
     * role : VOYAGEUR (par défaut) ou PARTENAIRE. Un administrateur ne s'inscrit pas : il est créé par un administrateur.
     * Pour un PARTENAIRE, nomSociete et typePartenaire sont obligatoires (compte EN_ATTENTE jusqu'à validation).
     */
    public record RegisterRequest(
            @NotBlank @Size(max = 80) String prenom,
            @NotBlank @Size(max = 80) String nom,
            @NotBlank @Email String email,
            @NotBlank @Size(min = 8, max = 72, message = "8 caractères minimum") String password,
            NomRole role,
            @Size(max = 120) String nomSociete,
            TypePartenaire typePartenaire,
            @Size(max = 60) String numeroFiscal) {
    }

    /** Création d'un compte par l'administrateur (n'importe quel rôle, compte ACTIF). */
    public record AdminCreateUserRequest(
            @NotBlank @Size(max = 80) String prenom,
            @NotBlank @Size(max = 80) String nom,
            @NotBlank @Email String email,
            @NotBlank @Size(min = 8, max = 72, message = "8 caractères minimum") String password,
            @NotNull NomRole role) {
    }

    public record LoginRequest(@NotBlank @Email String email, @NotBlank String password) {
    }

    public record UpdateProfileRequest(
            @Size(min = 1, max = 80) String prenom,
            @Size(min = 1, max = 80) String nom,
            @Size(max = 30) String telephone,
            @Size(max = 255) String photoProfil,
            @Size(max = 60) String nationalite,
            @Past LocalDate dateNaissance,
            @Size(max = 255) String adresse) {
    }

    public record ChangePasswordRequest(
            @NotBlank String ancienMotDePasse,
            @NotBlank @Size(min = 8, max = 72, message = "8 caractères minimum") String nouveauMotDePasse) {
    }

    public record PreferenceRequest(
            TypeVoyage typeVoyagePrefere,
            @Size(max = 100) String destinationPreferee,
            @Size(max = 30) String languePreferee,
            @Size(max = 10) String devisePreferee,
            @PositiveOrZero Double budgetMin,
            @PositiveOrZero Double budgetMax,
            @Positive Integer nombreVoyageursPrefere) {
    }

    public record PartnerProfileRequest(
            @NotBlank @Size(max = 120) String nomSociete,
            @NotNull TypePartenaire typePartenaire,
            @Size(max = 60) String numeroFiscal) {
    }

    // ---------- réponses
    /** Réponse de la connexion : le jeton JWT à envoyer ensuite dans l'en-tête Authorization: Bearer ... */
    public record AuthResponse(String token, String tokenType, long expiresIn, UserResponse user) {
    }

    public record UserResponse(Long id, String prenom, String nom, String email, String telephone,
                               String photoProfil, String nationalite, LocalDate dateNaissance,
                               String adresse, StatutCompte statut, List<String> roles,
                               PartenaireResponse partenaire, LocalDateTime dateCreation,
                               LocalDateTime dateModification) {
    }

    public record PartenaireResponse(Long id, String nomSociete, TypePartenaire typePartenaire, String numeroFiscal) {
    }

    public record PreferenceResponse(Long id, TypeVoyage typeVoyagePrefere, String destinationPreferee,
                                     String languePreferee, String devisePreferee, Double budgetMin,
                                     Double budgetMax, Integer nombreVoyageursPrefere) {
    }

    /** Réponse minimale pour les appels entre services (GET /internal/users/{id}). */
    public record UserIdentity(Long id, String email, StatutCompte statut, List<String> roles) {
    }

    public record PageResponse<T>(List<T> content, int page, int size, long totalElements, int totalPages) {
    }
}
