package com.travelmate.hebergement.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EquipementRequest {

    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    private String icone;
}
