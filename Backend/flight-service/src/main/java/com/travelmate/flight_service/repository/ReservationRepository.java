package com.travelmate.flight_service.repository;

import com.travelmate.flight_service.entity.Reservation;
import com.travelmate.flight_service.enums.StatutReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    Optional<Reservation> findByReference(String reference);

    boolean existsByReference(String reference);

    // Toutes les réservations d'un utilisateur (userId vient de user-service)
    List<Reservation> findByUserId(Long userId);

    List<Reservation> findByVolId(Long volId);

    List<Reservation> findByStatutReservation(StatutReservation statut);

    // Réservations à faire expirer (EN_ATTENTE dont la date limite est dépassée)
    List<Reservation> findByStatutReservationAndDateExpirationBefore(
            StatutReservation statut, LocalDateTime maintenant);
    // Import : java.util.Collection
    List<Reservation> findByVolIdAndStatutReservationIn(Long volId, Collection<StatutReservation> statuts);
    boolean existsByVolId(Long volId);
}