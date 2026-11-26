package com.travelmate.flight_service.repository;

import com.travelmate.flight_service.entity.Aeroport;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AeroportRepository extends JpaRepository<Aeroport, Long> {

    Optional<Aeroport> findByCodeAeroport(String codeAeroport);   // ex: "TUN"

    boolean existsByCodeAeroport(String codeAeroport);

    List<Aeroport> findByVilleIgnoreCase(String ville);

    List<Aeroport> findByPaysAeroportIgnoreCase(String paysAeroport);

    // Imports à ajouter : org.springframework.data.jpa.repository.Query, org.springframework.data.repository.query.Param
    @Query("""
        SELECT a FROM Aeroport a
        WHERE LOWER(a.codeAeroport) LIKE LOWER(CONCAT('%', :q, '%'))
           OR LOWER(a.nomAeroport)  LIKE LOWER(CONCAT('%', :q, '%'))
           OR LOWER(a.ville)        LIKE LOWER(CONCAT('%', :q, '%'))
        ORDER BY a.ville ASC
        """)
    List<Aeroport> rechercherParMotCle(@Param("q") String q);
}