package com.travelmate.transport.service;

import com.travelmate.transport.entity.ReservationTransport;
import com.travelmate.transport.repository.ReservationTransportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReservationTransportService {

    private final ReservationTransportRepository reservationRepository;

    public List<ReservationTransport> findAll() {
        return reservationRepository.findAll();
    }

    public Optional<ReservationTransport> findById(String id) {
        return reservationRepository.findById(id);
    }

    public ReservationTransport save(ReservationTransport reservation) {
        return reservationRepository.save(reservation);
    }

    public Optional<ReservationTransport> update(String id, ReservationTransport details) {
        return reservationRepository.findById(id).map(r -> {
            r.setDateDebut(details.getDateDebut());
            r.setDateFin(details.getDateFin());
            r.setPrixTotal(details.getPrixTotal());
            r.setStatut(details.getStatut());
            r.setVehiculeId(details.getVehiculeId());
            return reservationRepository.save(r);
        });
    }

    public boolean deleteById(String id) {
        if (reservationRepository.existsById(id)) {
            reservationRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
