package com.travelmate.payment_service.entity;

import com.travelmate.payment_service.enums.StatutRemboursement;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "remboursement")
@Getter @Setter @NoArgsConstructor
public class Remboursement {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull @ManyToOne(optional = false)
    @JoinColumn(name = "paiement_id", nullable = false)
    private Paiement paiement;

    @DecimalMin("0.01") @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal montantInitial;

    @DecimalMin("0.0") @DecimalMax("1.0") @Column(nullable = false, precision = 5, scale = 4)
    private BigDecimal tauxRetenue = BigDecimal.ZERO;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal montantRetenu = BigDecimal.ZERO;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal montantRembourse;

    private String motif;

    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20)
    private StatutRemboursement statut = StatutRemboursement.EN_ATTENTE;

    @Column(length = 100)
    private String referenceRemboursementPasserelle;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dateCreation = LocalDateTime.now();
}