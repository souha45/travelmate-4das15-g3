package com.travelmate.transport.controller;

import com.travelmate.transport.entity.ReservationTransport;
import com.travelmate.transport.service.ReservationTransportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ReservationTransportController {

    private final ReservationTransportService reservationService;

    @GetMapping
    public List<ReservationTransport> getAllReservations() {
        return reservationService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationTransport> getReservationById(@PathVariable String id) {
        return reservationService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ReservationTransport createReservation(@RequestBody ReservationTransport reservation) {
        return reservationService.save(reservation);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReservationTransport> updateReservation(@PathVariable String id,
                                                                   @RequestBody ReservationTransport details) {
        return reservationService.update(id, details)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable String id) {
        return reservationService.deleteById(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
