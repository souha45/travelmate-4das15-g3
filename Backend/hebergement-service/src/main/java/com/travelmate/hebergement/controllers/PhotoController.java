package com.travelmate.hebergement.controllers;

import com.travelmate.hebergement.dtos.PhotoRequest;
import com.travelmate.hebergement.dtos.PhotoResponse;
import com.travelmate.hebergement.entities.Photo;
import com.travelmate.hebergement.repositories.HebergementRepository;
import com.travelmate.hebergement.repositories.PhotoRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/accommodations/photos")
public class PhotoController {

    @Autowired
    private PhotoRepository photoRepository;

    @Autowired
    private HebergementRepository hebergementRepository;

    // GET - Lister toutes les photos
    @GetMapping
    public List<PhotoResponse> getAll() {
        return photoRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // POST - Créer une photo
    @PostMapping
    public ResponseEntity<PhotoResponse> create(@Valid @RequestBody PhotoRequest request) {
        return hebergementRepository.findById(request.getHebergementId()).map(hebergement -> {
            Photo photo = new Photo();
            photo.setUrl(request.getUrl());
            photo.setOrdre(request.getOrdre());
            photo.setHebergement(hebergement);
            Photo saved = photoRepository.save(photo);
            return ResponseEntity.ok(toResponse(saved));
        }).orElse(ResponseEntity.notFound().build());
    }

    // GET - Lire une photo par ID
    @GetMapping("/{id}")
    public ResponseEntity<PhotoResponse> getById(@PathVariable Long id) {
        return photoRepository.findById(id)
                .map(this::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // PUT - Mettre à jour une photo
    @PutMapping("/{id}")
    public ResponseEntity<PhotoResponse> update(@PathVariable Long id, @Valid @RequestBody PhotoRequest request) {
        return photoRepository.findById(id).map(existing -> {
            existing.setUrl(request.getUrl());
            existing.setOrdre(request.getOrdre());
            if (request.getHebergementId() != null) {
                hebergementRepository.findById(request.getHebergementId()).ifPresent(existing::setHebergement);
            }
            Photo saved = photoRepository.save(existing);
            return ResponseEntity.ok(toResponse(saved));
        }).orElse(ResponseEntity.notFound().build());
    }

    // DELETE - Supprimer une photo
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (photoRepository.existsById(id)) {
            photoRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    private PhotoResponse toResponse(Photo photo) {
        PhotoResponse response = new PhotoResponse();
        response.setId(photo.getId());
        response.setUrl(photo.getUrl());
        response.setOrdre(photo.getOrdre());
        if (photo.getHebergement() != null) {
            response.setHebergementId(photo.getHebergement().getId());
        }
        return response;
    }
}
