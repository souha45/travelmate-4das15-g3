package com.travelmate.transport.repository;

import com.travelmate.transport.entity.ReservationTransport;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationTransportRepository extends MongoRepository<ReservationTransport, String> {
}
