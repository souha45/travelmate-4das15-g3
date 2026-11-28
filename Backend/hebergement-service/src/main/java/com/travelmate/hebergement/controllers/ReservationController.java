package com.travelmate.hebergement.controllers;

import com.travelmate.hebergement.dtos.ReservationMapper;
import com.travelmate.hebergement.dtos.ReservationRequest;
import com.travelmate.hebergement.dtos.ReservationResponse;
import com.travelmate.hebergement.entities.ReservationHebergement;
import com.travelmate.hebergement.enums.StatutReservation;
import com.travelmate.hebergement.repositories.ChambreRepository;
import com.travelmate.hebergement.repositories.ReservationRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/accommodations/reservations")
public class ReservationController {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ChambreRepository chambreRepository;

    @Autowired
    private ReservationMapper mapper;

    // 1. Voir toutes les réservations
    @GetMapping
    public List<ReservationResponse> getAllReservations() {
        return reservationRepository.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    // 2. Lire une réservation par ID
    @GetMapping("/{id}")
    public ResponseEntity<ReservationResponse> getById(@PathVariable Long id) {
        return reservationRepository.findById(id)
                .map(mapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 3. Créer une réservation pour une chambre
    @PostMapping("/chambres/{chambreId}")
    public ResponseEntity<ReservationResponse> createReservation(@PathVariable Long chambreId, @Valid @RequestBody ReservationRequest request) {
        return chambreRepository.findById(chambreId).map(chambre -> {
            ReservationHebergement reservation = mapper.toEntity(request);
            reservation.setChambre(chambre);
            reservation.setDateCreation(LocalDateTime.now());
            if (reservation.getStatut() == null) {
                reservation.setStatut(StatutReservation.EN_ATTENTE);
            }
            ReservationHebergement saved = reservationRepository.save(reservation);
            return ResponseEntity.ok(mapper.toResponse(saved));
        }).orElse(ResponseEntity.notFound().build());
    }

    // 4. Mettre à jour une réservation (complète)
    @PutMapping("/{id}")
    public ResponseEntity<ReservationResponse> update(@PathVariable Long id, @Valid @RequestBody ReservationRequest request) {
        return reservationRepository.findById(id).map(existing -> {
            existing.setUserId(request.getUserId());
            existing.setPaymentId(request.getPaymentId());
            existing.setDateArrivee(request.getDateArrivee());
            existing.setDateDepart(request.getDateDepart());
            existing.setNbPersonnes(request.getNbPersonnes());
            existing.setPrixTotal(request.getPrixTotal());
            if (request.getStatut() != null) {
                existing.setStatut(request.getStatut());
            }
            ReservationHebergement saved = reservationRepository.save(existing);
            return ResponseEntity.ok(mapper.toResponse(saved));
        }).orElse(ResponseEntity.notFound().build());
    }

    // 5. Changer le statut d'une réservation (ex: Confirmer ou Annuler)
    @PutMapping("/{id}/statut")
    public ResponseEntity<ReservationResponse> updateStatut(@PathVariable Long id, @RequestParam StatutReservation nouveauStatut) {
        return reservationRepository.findById(id).map(reservation -> {
            reservation.setStatut(nouveauStatut);
            ReservationHebergement saved = reservationRepository.save(reservation);
            return ResponseEntity.ok(mapper.toResponse(saved));
        }).orElse(ResponseEntity.notFound().build());
    }

    // 6. Supprimer une réservation
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (reservationRepository.existsById(id)) {
            reservationRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}