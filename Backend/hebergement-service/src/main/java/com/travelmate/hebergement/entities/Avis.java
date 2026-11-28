package com.travelmate.hebergement.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Data @NoArgsConstructor @AllArgsConstructor
public class Avis {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private Integer note;
    private String commentaire;
    private LocalDateTime dateCreation;

    @ManyToOne
    @JoinColumn(name = "hebergement_id")
    @JsonIgnore
    private Hebergement hebergement;
}
