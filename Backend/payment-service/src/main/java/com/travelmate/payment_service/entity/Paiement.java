package com.travelmate.payment_service.entity;

import com.travelmate.payment_service.enums.MethodePaiement;
import com.travelmate.payment_service.enums.StatutPaiement;
import com.travelmate.payment_service.enums.TypeReservation;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "paiement", uniqueConstraints = @UniqueConstraint(name = "uk_paiement_reservation", columnNames = {"type_reservation", "reservation_id"}))
@Getter @Setter @NoArgsConstructor
public class Paiement {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 40)
    private String reference;

    @NotNull @Column(nullable = false)
    private Long reservationId;

    @Column(length = 40)
    private String reservationReference;

    @NotNull @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20)
    private TypeReservation typeReservation;

    @NotNull @Column(nullable = false)
    private Long utilisateurId;

    @NotNull @DecimalMin("0.01") @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal montant;

    @NotBlank @Size(min = 3, max = 3) @Column(nullable = false, length = 3)
    private String devise = "EUR";

    @Enumerated(EnumType.STRING) @Column(length = 20)
    private MethodePaiement methodePaiement;

    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 30)
    private StatutPaiement statut = StatutPaiement.EN_ATTENTE;

    @Column(length = 100)
    private String referenceTransactionPasserelle;

    @Column(unique = true, length = 64)
    private String cleIdempotence;

    private String motifEchec;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    @Column(name = "date_mise_a_jour")
    private LocalDateTime dateMiseAJour;

    @PrePersist void onCreate() { dateCreation = LocalDateTime.now(); dateMiseAJour = dateCreation; }
    @PreUpdate void onUpdate() { dateMiseAJour = LocalDateTime.now(); }
}