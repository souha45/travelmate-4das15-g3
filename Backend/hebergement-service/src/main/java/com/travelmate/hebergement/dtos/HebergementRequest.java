package com.travelmate.hebergement.dtos;

import com.travelmate.hebergement.enums.TypeHebergement;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class HebergementRequest {

    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    @NotNull(message = "Le type est obligatoire")
    private TypeHebergement type;

    private String description;
    private String adresse;
    private String ville;
    private String pays;
    private Double latitude;
    private Double longitude;
    private Integer etoiles;

    @NotNull(message = "L'ID du partenaire est obligatoire")
    private Long partenaireId;
}