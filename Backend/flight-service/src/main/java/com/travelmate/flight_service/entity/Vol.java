package com.travelmate.flight_service.entity;

import com.travelmate.flight_service.enums.StatutModeration;
import com.travelmate.flight_service.enums.StatutVol;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Vol {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 20)
    private String numeroVol;
    private LocalDateTime dateHeureDepart;
    private LocalDateTime dateHeureArrivee;
    private BigDecimal prix;
    private int nbrePlacesTotal;
    private int nbrePlacesDispo;

    @Enumerated(EnumType.STRING)
    private StatutVol statutVol;
    @Enumerated(EnumType.STRING)
    private StatutModeration moderation;

    @Version
    private Long version; // évite la sur-réservation en cas d'accès concurrents

    @ManyToOne(optional = false)
    @JoinColumn(name = "compagnie_id")
    private Compagnie compagnie;

    @ManyToOne(optional = false)
    @JoinColumn(name = "aeroport_depart_id")
    private Aeroport aeroportDepart;

    @ManyToOne(optional = false)
    @JoinColumn(name = "aeroport_arrivee_id")
    private Aeroport aeroportArrivee;

    public boolean estDisponible(int n) { return nbrePlacesDispo >= n; }

    public void reserverPlaces(int n) {
        if (!estDisponible(n)) throw new IllegalStateException("Places insuffisantes");
        nbrePlacesDispo -= n;
    }

    public void libererPlaces(int n) {
        nbrePlacesDispo = Math.min(nbrePlacesTotal, nbrePlacesDispo + n);
    }

    public void annuler() { this.statutVol = StatutVol.ANNULE; }
}