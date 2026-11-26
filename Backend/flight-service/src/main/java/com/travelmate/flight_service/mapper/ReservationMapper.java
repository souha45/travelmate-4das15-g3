package com.travelmate.flight_service.mapper;

import com.travelmate.flight_service.dto.ReservationResponse;
import com.travelmate.flight_service.entity.Reservation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReservationMapper {

    private final VolMapper volMapper;
    private final PassagerMapper passagerMapper;

    public ReservationResponse toResponse(Reservation r) {
        return new ReservationResponse(
                r.getId(), r.getReference(), r.getUserId(), r.getPaiementId(),
                r.getStatutReservation(), r.getMontantTotal(), r.getNbrePassagers(),
                r.getDateCreation(), r.getDateExpiration(),
                volMapper.toResponse(r.getVol()),
                r.getPassagers().stream().map(passagerMapper::toResponse).toList());
    }
}