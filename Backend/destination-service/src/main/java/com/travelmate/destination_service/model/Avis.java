package com.travelmate.destination_service.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "avis")
@CompoundIndex(name = "un_avis_par_utilisateur_et_activite",
        def = "{'utilisateurId': 1, 'activiteId': 1}", unique = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Avis {

    @Id
    private String id;

    private String activiteId;
    private String utilisateurId;
    private String nomUtilisateur;

    @Min(1)
    @Max(5)
    private int note;

    private String commentaire;

    @Builder.Default
    private LocalDateTime dateCreation = LocalDateTime.now();
}