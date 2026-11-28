package com.travelmate.hebergement.entities;

import com.travelmate.hebergement.enums.StatutReservation;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data @NoArgsConstructor @AllArgsConstructor
public class ReservationHebergement {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private Long paymentId;
    private LocalDate dateArrivee;
    private LocalDate dateDepart;
    private Integer nbPersonnes;
    private BigDecimal prixTotal;
    @Enumerated(EnumType.STRING)
    private StatutReservation statut;
    private LocalDateTime dateCreation;

    @ManyToOne
    @JoinColumn(name = "chambre_id")
    private Chambre chambre;
}