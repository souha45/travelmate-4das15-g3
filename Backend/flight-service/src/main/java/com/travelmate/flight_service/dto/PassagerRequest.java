package com.travelmate.flight_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

@Schema(description = "Passager d'une réservation")
public record PassagerRequest(

        @Schema(example = "Souha")
        @NotBlank(message = "Le prénom est obligatoire")
        @Size(max = 50, message = "Le prénom ne doit pas dépasser 50 caractères")
        String prenom,

        @Schema(example = "Khenissi")
        @NotBlank(message = "Le nom est obligatoire")
        @Size(max = 50, message = "Le nom ne doit pas dépasser 50 caractères")
        String nom,

        @Schema(example = "2000-01-01")
        @NotNull(message = "La date de naissance est obligatoire")
        @Past(message = "La date de naissance doit être dans le passé")
        LocalDate dateNaissance,

        @Schema(example = "P1234567")
        @NotBlank(message = "Le numéro de passeport est obligatoire")
        @Pattern(regexp = "^[A-Za-z0-9]{6,12}$", message = "Le passeport doit contenir 6 à 12 lettres ou chiffres")
        String numPasseport,

        @Schema(example = "Tunisienne")
        @NotBlank(message = "La nationalité est obligatoire")
        @Size(max = 50, message = "La nationalité ne doit pas dépasser 50 caractères")
        String nationalite,

        @Schema(example = "12A", description = "Optionnel. Format : 1 à 3 chiffres + une lettre A-F")
        @Pattern(regexp = "^[0-9]{1,3}[A-Fa-f]$", message = "Siège invalide (exemple : 12A)")
        String numeroSiege) {
}