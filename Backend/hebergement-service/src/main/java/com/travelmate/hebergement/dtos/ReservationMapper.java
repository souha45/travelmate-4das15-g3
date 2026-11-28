package com.travelmate.hebergement.dtos;

import com.travelmate.hebergement.entities.ReservationHebergement;
import org.springframework.stereotype.Component;

@Component
public class ReservationMapper {

    public ReservationHebergement toEntity(ReservationRequest request) {
        ReservationHebergement reservation = new ReservationHebergement();
        reservation.setUserId(request.getUserId());
        reservation.setPaymentId(request.getPaymentId());
        reservation.setDateArrivee(request.getDateArrivee());
        reservation.setDateDepart(request.getDateDepart());
        reservation.setNbPersonnes(request.getNbPersonnes());
        reservation.setPrixTotal(request.getPrixTotal());
        reservation.setStatut(request.getStatut());
        return reservation;
    }

    public ReservationResponse toResponse(ReservationHebergement entity) {
        ReservationResponse response = new ReservationResponse();
        response.setId(entity.getId());
        response.setUserId(entity.getUserId());
        response.setPaymentId(entity.getPaymentId());
        response.setDateArrivee(entity.getDateArrivee());
        response.setDateDepart(entity.getDateDepart());
        response.setNbPersonnes(entity.getNbPersonnes());
        response.setPrixTotal(entity.getPrixTotal());
        response.setStatut(entity.getStatut());
        response.setDateCreation(entity.getDateCreation());
        if (entity.getChambre() != null) {
            response.setChambreId(entity.getChambre().getId());
        }
        return response;
    }
}
