package com.travelmate.transport.repository;

import com.travelmate.transport.entity.Avis;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AvisRepository extends MongoRepository<Avis, String> {
}
