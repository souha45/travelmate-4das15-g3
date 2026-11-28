package com.travelmate.flight_service.entity;

import com.travelmate.flight_service.enums.StatutCompagnie;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Compagnie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nomCompagnie;
    private String codeCompagnie;
    private String paysCompagnie;

    @Column(name = "partenaire_id", nullable = false, unique = true)
    private Long partenaireId;   // id du compte PARTENAIRE dans user-service (pas de clé étrangère)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutCompagnie statut = StatutCompagnie.ACTIVE;

    @OneToMany(mappedBy = "compagnie")
    private List<Vol> vols = new ArrayList<>();
}