package com.travelmate.hebergement.entities;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data @NoArgsConstructor @AllArgsConstructor
public class Disponibilite {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate date;
    private Integer quantiteRestante;
    private BigDecimal prix;

    @ManyToOne
    @JoinColumn(name = "chambre_id")
    private Chambre chambre;
}