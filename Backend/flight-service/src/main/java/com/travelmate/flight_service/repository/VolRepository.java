package com.travelmate.flight_service.repository;

import com.travelmate.flight_service.entity.Vol;
import com.travelmate.flight_service.enums.StatutModeration;
import com.travelmate.flight_service.enums.StatutVol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VolRepository extends JpaRepository<Vol, Long> {

    Optional<Vol> findByNumeroVol(String numeroVol);

    boolean existsByNumeroVol(String numeroVol);

    List<Vol> findByCompagnieId(Long compagnieId);

    List<Vol> findByModeration(StatutModeration moderation);

    List<Vol> findByStatutVol(StatutVol statutVol);

    boolean existsByCompagnieId(Long compagnieId);

    boolean existsByAeroportDepartIdOrAeroportArriveeId(Long departId, Long arriveeId);

    // Recherche publique : départ + arrivée + intervalle de dates + nb de places
    @Query("""
            SELECT v FROM Vol v
            WHERE v.aeroportDepart.codeAeroport = :codeDepart
              AND v.aeroportArrivee.codeAeroport = :codeArrivee
              AND v.dateHeureDepart BETWEEN :debut AND :fin
              AND v.nbrePlacesDispo >= :nbPlaces
              AND v.moderation = com.travelmate.flight_service.enums.StatutModeration.APPROUVE
              AND v.statutVol = com.travelmate.flight_service.enums.StatutVol.PROGRAMME
              AND v.moderation = com.travelmate.flight_service.enums.StatutModeration.APPROUVE
              AND v.compagnie.statut = com.travelmate.flight_service.enums.StatutCompagnie.ACTIVE
            ORDER BY v.dateHeureDepart ASC
            """)
    List<Vol> rechercherVols(@Param("codeDepart") String codeDepart,
                             @Param("codeArrivee") String codeArrivee,
                             @Param("debut") LocalDateTime debut,
                             @Param("fin") LocalDateTime fin,
                             @Param("nbPlaces") int nbPlaces);


}