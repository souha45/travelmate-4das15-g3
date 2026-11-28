package com.travelmate.flight_service.repository;

import com.travelmate.flight_service.entity.Compagnie;
import com.travelmate.flight_service.enums.StatutCompagnie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompagnieRepository extends JpaRepository<Compagnie, Long> {

    // Retrouver une compagnie par son code (ex: "TU" pour Tunisair)
    Optional<Compagnie> findByCodeCompagnie(String codeCompagnie);

    // Utile pour refuser un doublon à la création (renvoyer 409 Conflict)
    boolean existsByCodeCompagnie(String codeCompagnie);

    // Filtrer par pays
    List<Compagnie> findByPaysCompagnieIgnoreCase(String paysCompagnie);

    Optional<Compagnie> findByPartenaireId(Long partenaireId);
    boolean existsByPartenaireId(Long partenaireId);
    List<Compagnie> findByStatut(StatutCompagnie statut);
}