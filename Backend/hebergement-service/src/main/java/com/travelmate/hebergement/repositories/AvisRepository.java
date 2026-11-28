package com.travelmate.hebergement.repositories;

import com.travelmate.hebergement.entities.Avis;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AvisRepository extends JpaRepository<Avis, Long> {
}
