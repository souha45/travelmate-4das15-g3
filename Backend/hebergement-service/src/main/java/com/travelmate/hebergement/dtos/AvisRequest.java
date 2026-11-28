package com.travelmate.hebergement.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AvisRequest {

    @NotNull(message = "L'ID utilisateur est obligatoire")
    private Long userId;

    @NotNull(message = "La note est obligatoire")
    @Min(value = 1, message = "La note doit être entre 1 et 5")
    @Max(value = 5, message = "La note doit être entre 1 et 5")
    private Integer note;

    @NotBlank(message = "Le commentaire est obligatoire")
    private String commentaire;

    private Long hebergementId;
}
