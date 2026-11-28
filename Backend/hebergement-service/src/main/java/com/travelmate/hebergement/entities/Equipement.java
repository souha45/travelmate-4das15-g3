package com.travelmate.hebergement.entities;

import jakarta.persistence.*;
import lombok.*;
import java.util.Set;

@Entity
@Data @NoArgsConstructor @AllArgsConstructor
public class Equipement {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nom;
    private String icone;

    @ManyToMany(mappedBy = "equipements")
    private Set<Hebergement> hebergements;
}
