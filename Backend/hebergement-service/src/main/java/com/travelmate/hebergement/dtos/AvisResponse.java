package com.travelmate.hebergement.dtos;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AvisResponse {
    private Long id;
    private Long userId;
    private Integer note;
    private String commentaire;
    private LocalDateTime dateCreation;
    private Long hebergementId;
}
