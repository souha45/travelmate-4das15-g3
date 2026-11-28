package com.travelmate.hebergement.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.travelmate.hebergement.enums.TypeHebergement;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;
import java.util.Set;

@Entity
@Data @NoArgsConstructor @AllArgsConstructor
public class Hebergement {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nom;
    @Enumerated(EnumType.STRING)
    private TypeHebergement type;
    @Column(length = 1000)
    private String description;
    private String adresse;
    private String ville;
    private String pays;
    private Double latitude;
    private Double longitude;
    private Integer etoiles;
    private Long partenaireId;

    @OneToMany(mappedBy = "hebergement", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Chambre> chambres;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "hebergement_equipement",
        joinColumns = @JoinColumn(name = "hebergement_id"),
        inverseJoinColumns = @JoinColumn(name = "equipement_id")
    )
    @JsonIgnore
    private Set<Equipement> equipements;

    @OneToMany(mappedBy = "hebergement", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Photo> photos;

    @OneToMany(mappedBy = "hebergement", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Avis> avis;
}