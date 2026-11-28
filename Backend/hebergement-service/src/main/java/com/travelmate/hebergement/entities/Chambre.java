package com.travelmate.hebergement.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.travelmate.hebergement.enums.TypeChambre;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Data @NoArgsConstructor @AllArgsConstructor
public class Chambre {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private TypeChambre type;
    private Integer capacite;
    private BigDecimal prixParNuit;
    private Integer nombreTotal;
    private String description;

    @ManyToOne
    @JoinColumn(name = "hebergement_id")
    @JsonIgnore
    private Hebergement hebergement;

    @OneToMany(mappedBy = "chambre", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Disponibilite> disponibilites;
}