package com.travelmate.transport.repository;

import com.travelmate.transport.entity.Vehicule;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VehiculeRepository extends MongoRepository<Vehicule, String> {
}
