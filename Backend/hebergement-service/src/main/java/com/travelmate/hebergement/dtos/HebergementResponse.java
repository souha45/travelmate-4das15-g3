package com.travelmate.hebergement.dtos;

import com.travelmate.hebergement.enums.TypeHebergement;
import lombok.Data;

@Data
public class HebergementResponse {
    private Long id;
    private String nom;
    private TypeHebergement type;
    private String description;
    private String adresse;
    private String ville;
    private String pays;
    private Double latitude;
    private Double longitude;
    private Integer etoiles;
    private Long partenaireId;
}