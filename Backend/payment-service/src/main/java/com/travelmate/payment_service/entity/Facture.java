package com.travelmate.payment_service.entity;

import com.travelmate.payment_service.enums.StatutFacture;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "facture")
@Getter @Setter @NoArgsConstructor
public class Facture {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String numeroFacture;

    @NotNull @OneToOne(optional = false)
    @JoinColumn(name = "paiement_id", nullable = false, unique = true)
    private Paiement paiement;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal montantHt;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal montantTva;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal montantTtc;

    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20)
    private StatutFacture statut = StatutFacture.BROUILLON;

    @Column(nullable = false)
    private LocalDateTime dateEmission;

    @Valid @NotEmpty
    @OneToMany(mappedBy = "facture", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<LigneFacture> lignes = new ArrayList<>();

    public void ajouterLigne(LigneFacture ligne) {
        ligne.setFacture(this);
        lignes.add(ligne);
    }
}