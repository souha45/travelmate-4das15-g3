package com.travelmate.destination_service.repository;

import com.travelmate.destination_service.model.Avis;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface AvisRepository extends MongoRepository<Avis, String> {
    Page<Avis> findByActiviteId(String activiteId, Pageable pageable);
    List<Avis> findAllByActiviteId(String activiteId);
    List<Avis> findByUtilisateurId(String utilisateurId);
    boolean existsByUtilisateurIdAndActiviteId(String utilisateurId, String activiteId);
    void deleteByActiviteId(String activiteId);
}
