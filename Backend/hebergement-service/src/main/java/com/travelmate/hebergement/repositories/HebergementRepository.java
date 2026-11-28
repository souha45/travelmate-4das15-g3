package com.travelmate.hebergement.repositories;

import com.travelmate.hebergement.entities.Hebergement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HebergementRepository extends JpaRepository<Hebergement, Long> {
}