package com.travelmate.hebergement.repositories;

import com.travelmate.hebergement.entities.Disponibilite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DisponibiliteRepository extends JpaRepository<Disponibilite, Long> {
}