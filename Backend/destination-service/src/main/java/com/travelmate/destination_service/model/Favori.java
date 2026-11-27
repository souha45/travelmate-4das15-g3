package com.travelmate.destination_service.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "favoris")
@CompoundIndex(name = "un_favori_par_utilisateur_et_destination",
        def = "{'utilisateurId': 1, 'destinationId': 1}", unique = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Favori {

    @Id
    private String id;

    private String utilisateurId;
    private String destinationId;

    @Builder.Default
    private LocalDateTime dateAjout = LocalDateTime.now();
}
