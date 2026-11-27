package com.travelmate.destination_service.repository;

import com.travelmate.destination_service.model.Favori;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface FavoriRepository extends MongoRepository<Favori, String> {
    List<Favori> findByUtilisateurId(String utilisateurId);
    Optional<Favori> findByUtilisateurIdAndDestinationId(String utilisateurId, String destinationId);
    boolean existsByUtilisateurIdAndDestinationId(String utilisateurId, String destinationId);
    void deleteByUtilisateurId(String utilisateurId);
}
