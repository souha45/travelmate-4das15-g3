package com.travelmate.destination_service.model;

import com.travelmate.destination_service.enums.Categorie;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "activites")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Activite {

    @Id
    private String id;

    @Indexed
    private String destinationId;

    private String nom;
    private String description;

    @Indexed
    private Categorie categorie;

    private double prix;
    private String devise;
    private String lieu;
    private String horaires;
    private List<String> images;

    @Builder.Default
    private double note = 0;

    @Builder.Default
    private int nombreAvis = 0;
}
