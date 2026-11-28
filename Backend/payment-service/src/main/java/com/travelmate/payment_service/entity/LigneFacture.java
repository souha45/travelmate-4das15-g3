package com.travelmate.payment_service.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Entity
@Table(name = "ligne_facture")
@Getter @Setter @NoArgsConstructor
public class LigneFacture {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "facture_id", nullable = false)
    private Facture facture;

    @NotBlank @Column(nullable = false)
    private String designation;

    @Min(1) @Column(nullable = false)
    private int quantite = 1;

    @NotNull @DecimalMin("0.0") @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal prixUnitaire;

    public BigDecimal getSousTotal() {
        return prixUnitaire == null ? BigDecimal.ZERO : prixUnitaire.multiply(BigDecimal.valueOf(quantite));
    }
}