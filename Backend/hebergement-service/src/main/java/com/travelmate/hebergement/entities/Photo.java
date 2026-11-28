package com.travelmate.hebergement.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data @NoArgsConstructor @AllArgsConstructor
public class Photo {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String url;
    private Integer ordre;

    @ManyToOne
    @JoinColumn(name = "hebergement_id")
    @JsonIgnore
    private Hebergement hebergement;
}
