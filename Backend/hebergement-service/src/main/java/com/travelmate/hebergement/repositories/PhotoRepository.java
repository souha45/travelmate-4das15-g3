package com.travelmate.hebergement.repositories;

import com.travelmate.hebergement.entities.Photo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhotoRepository extends JpaRepository<Photo, Long> {
}
