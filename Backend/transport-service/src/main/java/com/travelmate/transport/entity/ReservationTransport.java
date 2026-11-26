package com.travelmate.transport.entity;

import com.travelmate.transport.entity.enums.StatutReservation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document(collection = "reservations_transport")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationTransport {

    @Id
    private String id;

    private LocalDate dateDebut;
    private LocalDate dateFin;
    private double prixTotal;
    private StatutReservation statut;
    private String vehiculeId;
}
