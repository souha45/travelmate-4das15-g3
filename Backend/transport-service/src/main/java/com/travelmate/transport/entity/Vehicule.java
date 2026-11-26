package com.travelmate.transport.entity;

import com.travelmate.transport.entity.enums.CategorieVehicule;
import com.travelmate.transport.entity.enums.StatutVehicule;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "vehicules")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Vehicule {

    @Id
    private String id;

    private String marque;
    private String modele;
    private String immatriculation;
    private int nombrePlaces;
    private double prixParJour;
    private CategorieVehicule categorie;
    private StatutVehicule statut;
}
