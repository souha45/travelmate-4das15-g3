package com.travelmate.hebergement.dtos;

import com.travelmate.hebergement.enums.TypeChambre;
import lombok.Data;

@Data
public class ChambreResponse {
    private Long id;
    private TypeChambre type;
    private Integer capacite;
    private java.math.BigDecimal prixParNuit;
    private Integer nombreTotal;
    private String description;
    private Long hebergementId;
}
