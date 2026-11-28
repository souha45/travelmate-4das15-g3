package com.travelmate.hebergement.controllers;

import com.travelmate.hebergement.dtos.AvisRequest;
import com.travelmate.hebergement.dtos.AvisResponse;
import com.travelmate.hebergement.entities.Avis;
import com.travelmate.hebergement.repositories.AvisRepository;
import com.travelmate.hebergement.repositories.HebergementRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/accommodations/avis")
public class AvisController {

    @Autowired
    private AvisRepository avisRepository;

    @Autowired
    private HebergementRepository hebergementRepository;

    // GET - Lister tous les avis
    @GetMapping
    public List<AvisResponse> getAll() {
        return avisRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // POST - Créer un avis
    @PostMapping
    public ResponseEntity<AvisResponse> create(@Valid @RequestBody AvisRequest request) {
        return hebergementRepository.findById(request.getHebergementId()).map(hebergement -> {
            Avis avis = new Avis();
            avis.setUserId(request.getUserId());
            avis.setNote(request.getNote());
            avis.setCommentaire(request.getCommentaire());
            avis.setDateCreation(LocalDateTime.now());
            avis.setHebergement(hebergement);
            Avis saved = avisRepository.save(avis);
            return ResponseEntity.ok(toResponse(saved));
        }).orElse(ResponseEntity.notFound().build());
    }

    // GET - Lire un avis par ID
    @GetMapping("/{id}")
    public ResponseEntity<AvisResponse> getById(@PathVariable Long id) {
        return avisRepository.findById(id)
                .map(this::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // PUT - Mettre à jour un avis
    @PutMapping("/{id}")
    public ResponseEntity<AvisResponse> update(@PathVariable Long id, @Valid @RequestBody AvisRequest request) {
        return avisRepository.findById(id).map(existing -> {
            existing.setUserId(request.getUserId());
            existing.setNote(request.getNote());
            existing.setCommentaire(request.getCommentaire());
            if (request.getHebergementId() != null) {
                hebergementRepository.findById(request.getHebergementId()).ifPresent(existing::setHebergement);
            }
            Avis saved = avisRepository.save(existing);
            return ResponseEntity.ok(toResponse(saved));
        }).orElse(ResponseEntity.notFound().build());
    }

    // DELETE - Supprimer un avis
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (avisRepository.existsById(id)) {
            avisRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    private AvisResponse toResponse(Avis avis) {
        AvisResponse response = new AvisResponse();
        response.setId(avis.getId());
        response.setUserId(avis.getUserId());
        response.setNote(avis.getNote());
        response.setCommentaire(avis.getCommentaire());
        response.setDateCreation(avis.getDateCreation());
        if (avis.getHebergement() != null) {
            response.setHebergementId(avis.getHebergement().getId());
        }
        return response;
    }
}
