package com.travelmate.flight_service.entity;

import com.travelmate.flight_service.enums.StatutReservation;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String reference;
    private Long userId;      // référence vers user-service (pas de clé étrangère)
    private Long paiementId;  // référence vers payment-service

    @Enumerated(EnumType.STRING)
    private StatutReservation statutReservation;
    private BigDecimal montantTotal;
    private int nbrePassagers;
    private LocalDateTime dateCreation;
    private LocalDateTime dateExpiration;

    @ManyToOne(optional = false)
    @JoinColumn(name = "vol_id")
    private Vol vol;

    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Passager> passagers = new ArrayList<>();

    public void confirmer() { this.statutReservation = StatutReservation.CONFIRMEE; }
    public void annuler()   { this.statutReservation = StatutReservation.ANNULEE; }
    public void expirer()   { this.statutReservation = StatutReservation.EXPIREE; }
}
