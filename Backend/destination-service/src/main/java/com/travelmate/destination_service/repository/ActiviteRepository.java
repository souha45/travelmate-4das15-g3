package com.travelmate.destination_service.repository;

import com.travelmate.destination_service.model.Activite;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ActiviteRepository extends MongoRepository<Activite, String> {}
