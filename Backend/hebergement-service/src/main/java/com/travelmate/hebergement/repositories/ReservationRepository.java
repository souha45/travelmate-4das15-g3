package com.travelmate.hebergement.repositories;

import com.travelmate.hebergement.entities.ReservationHebergement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepository extends JpaRepository<ReservationHebergement, Long> {
}