package com.travelmate.payment_service.repository;

import com.travelmate.payment_service.entity.Facture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface FactureRepository extends JpaRepository<Facture, Long> {
    @Query("SELECT DISTINCT f FROM Facture f JOIN FETCH f.paiement p LEFT JOIN FETCH f.lignes WHERE p.utilisateurId = :userId ORDER BY f.dateEmission DESC")
    List<Facture> findByUtilisateur(@Param("userId") Long userId);
}