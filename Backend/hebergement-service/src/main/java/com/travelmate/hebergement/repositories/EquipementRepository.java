package com.travelmate.hebergement.repositories;

import com.travelmate.hebergement.entities.Equipement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EquipementRepository extends JpaRepository<Equipement, Long> {
}
