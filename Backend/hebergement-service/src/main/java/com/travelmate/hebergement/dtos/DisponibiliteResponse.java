package com.travelmate.hebergement.dtos;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class DisponibiliteResponse {
    private Long id;
    private LocalDate date;
    private Integer quantiteRestante;
    private BigDecimal prix;
    private Long chambreId;
}
