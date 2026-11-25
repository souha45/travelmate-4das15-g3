package com.travelmate.flight_service.repository;

import com.travelmate.flight_service.entity.Passager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PassagerRepository extends JpaRepository<Passager, Long> {

    List<Passager> findByReservationId(Long reservationId);

    List<Passager> findByNumPasseport(String numPasseport);

    // Siège déjà occupé sur ce vol par une réservation encore active ?
    @Query("""
            SELECT COUNT(p) > 0 FROM Passager p
            WHERE p.reservation.vol.id = :volId
              AND p.numeroSiege = :numeroSiege
              AND p.reservation.statutReservation IN
                  (com.travelmate.flight_service.enums.StatutReservation.EN_ATTENTE,
                   com.travelmate.flight_service.enums.StatutReservation.CONFIRMEE)
            """)
    boolean siegeDejaOccupe(@Param("volId") Long volId,
                            @Param("numeroSiege") String numeroSiege);
}