package com.travelmate.hebergement.dtos;

import com.travelmate.hebergement.enums.TypeChambre;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChambreRequest {

    @NotNull(message = "Le type est obligatoire")
    private TypeChambre type;

    @NotNull(message = "La capacité est obligatoire")
    @Min(value = 1, message = "La capacité doit être au moins 1")
    private Integer capacite;

    @NotNull(message = "Le prix par nuit est obligatoire")
    @Min(value = 0, message = "Le prix par nuit doit être positif")
    private java.math.BigDecimal prixParNuit;

    @NotNull(message = "Le nombre total est obligatoire")
    @Min(value = 1, message = "Le nombre total doit être au moins 1")
    private Integer nombreTotal;

    private String description;
}
