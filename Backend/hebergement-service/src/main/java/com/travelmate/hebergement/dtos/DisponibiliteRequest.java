package com.travelmate.hebergement.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class DisponibiliteRequest {

    @NotNull(message = "La date est obligatoire")
    private LocalDate date;

    @NotNull(message = "La quantité restante est obligatoire")
    @Min(value = 0, message = "La quantité restante doit être positive")
    private Integer quantiteRestante;

    @NotNull(message = "Le prix est obligatoire")
    @Min(value = 0, message = "Le prix doit être positif")
    private java.math.BigDecimal prix;
}
