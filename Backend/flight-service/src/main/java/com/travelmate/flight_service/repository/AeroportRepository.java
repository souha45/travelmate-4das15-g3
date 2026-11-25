package com.travelmate.flight_service.repository;

import com.travelmate.flight_service.entity.Aeroport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AeroportRepository extends JpaRepository<Aeroport, Long> {

    Optional<Aeroport> findByCodeAeroport(String codeAeroport);   // ex: "TUN"

    boolean existsByCodeAeroport(String codeAeroport);

    List<Aeroport> findByVilleIgnoreCase(String ville);

    List<Aeroport> findByPaysAeroportIgnoreCase(String paysAeroport);
}