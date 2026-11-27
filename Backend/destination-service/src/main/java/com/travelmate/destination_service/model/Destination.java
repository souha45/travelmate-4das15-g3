package com.travelmate.destination_service.model;

import com.travelmate.destination_service.enums.TypeDestination;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "destinations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Destination {

    @Id
    private String id;

    @TextIndexed
    private String nom;

    private TypeDestination type;

    @Indexed
    private String pays;

    private String codePays;
    private String description;
    private String climat;
    private String meilleureSaison;
    private String langue;
    private String devise;
    private String fuseauHoraire;
    private List<String> images;
    private Coordonnees coordonnees;

    @Builder.Default
    private boolean actif = true;
}
