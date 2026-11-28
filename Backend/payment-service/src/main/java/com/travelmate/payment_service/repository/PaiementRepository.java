package com.travelmate.payment_service.repository;

import com.travelmate.payment_service.entity.Paiement;
import com.travelmate.payment_service.enums.StatutPaiement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface PaiementRepository extends JpaRepository<Paiement, Long> {
    Optional<Paiement> findByReference(String reference);

    @Query("SELECT p FROM Paiement p WHERE (:userId IS NULL OR p.utilisateurId = :userId) AND (:statut IS NULL OR p.statut = :statut) ORDER BY p.dateCreation DESC")
    List<Paiement> rechercher(@Param("userId") Long userId, @Param("statut") StatutPaiement statut);
}