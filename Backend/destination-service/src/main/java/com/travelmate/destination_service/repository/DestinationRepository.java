package com.travelmate.destination_service.repository;

import com.travelmate.destination_service.model.Destination;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DestinationRepository extends MongoRepository<Destination, String> {
    boolean existsByNomIgnoreCaseAndPaysIgnoreCase(String nom, String pays);
}
