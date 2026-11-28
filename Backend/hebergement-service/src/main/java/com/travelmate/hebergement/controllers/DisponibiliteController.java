package com.travelmate.hebergement.controllers;

import com.travelmate.hebergement.dtos.DisponibiliteMapper;
import com.travelmate.hebergement.dtos.DisponibiliteRequest;
import com.travelmate.hebergement.dtos.DisponibiliteResponse;
import com.travelmate.hebergement.entities.Disponibilite;
import com.travelmate.hebergement.repositories.ChambreRepository;
import com.travelmate.hebergement.repositories.DisponibiliteRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/accommodations")
public class DisponibiliteController {

    @Autowired
    private DisponibiliteRepository disponibiliteRepository;

    @Autowired
    private ChambreRepository chambreRepository;

    @Autowired
    private DisponibiliteMapper mapper;

    // 1. Voir les dispos d'une chambre
    @GetMapping("/chambres/{chambreId}/disponibilites")
    public ResponseEntity<List<DisponibiliteResponse>> getDisponibilitesByChambre(@PathVariable Long chambreId) {
        if (!chambreRepository.existsById(chambreId)) {
            return ResponseEntity.notFound().build();
        }
        List<Disponibilite> dispos = disponibiliteRepository.findAll().stream()
                .filter(d -> d.getChambre().getId().equals(chambreId))
                .toList();
        List<DisponibiliteResponse> responses = dispos.stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    // 2. Ajouter une disponibilité à une chambre
    @PostMapping("/chambres/{chambreId}/disponibilites")
    public ResponseEntity<DisponibiliteResponse> createDisponibilite(@PathVariable Long chambreId, @Valid @RequestBody DisponibiliteRequest request) {
        return chambreRepository.findById(chambreId).map(chambre -> {
            Disponibilite disponibilite = mapper.toEntity(request);
            disponibilite.setChambre(chambre);
            Disponibilite saved = disponibiliteRepository.save(disponibilite);
            return ResponseEntity.ok(mapper.toResponse(saved));
        }).orElse(ResponseEntity.notFound().build());
    }

    // 3. Lire une disponibilité par ID
    @GetMapping("/disponibilites/{id}")
    public ResponseEntity<DisponibiliteResponse> getById(@PathVariable Long id) {
        return disponibiliteRepository.findById(id)
                .map(mapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 4. Mettre à jour une disponibilité
    @PutMapping("/disponibilites/{id}")
    public ResponseEntity<DisponibiliteResponse> update(@PathVariable Long id, @Valid @RequestBody DisponibiliteRequest request) {
        return disponibiliteRepository.findById(id).map(existing -> {
            existing.setDate(request.getDate());
            existing.setQuantiteRestante(request.getQuantiteRestante());
            existing.setPrix(request.getPrix());
            Disponibilite saved = disponibiliteRepository.save(existing);
            return ResponseEntity.ok(mapper.toResponse(saved));
        }).orElse(ResponseEntity.notFound().build());
    }

    // 5. Supprimer une disponibilité
    @DeleteMapping("/disponibilites/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (disponibiliteRepository.existsById(id)) {
            disponibiliteRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}