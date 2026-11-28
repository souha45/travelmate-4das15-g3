package com.travelmate.payment_service.repository;

import com.travelmate.payment_service.entity.Remboursement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface RemboursementRepository extends JpaRepository<Remboursement, Long> {
    @Query("SELECT r FROM Remboursement r JOIN FETCH r.paiement p WHERE (:paiementId IS NULL OR p.id = :paiementId) ORDER BY r.dateCreation DESC")
    List<Remboursement> rechercher(@Param("paiementId") Long paiementId);
}