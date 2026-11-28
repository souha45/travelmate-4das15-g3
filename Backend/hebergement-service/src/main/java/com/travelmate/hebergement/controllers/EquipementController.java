package com.travelmate.hebergement.controllers;

import com.travelmate.hebergement.dtos.EquipementRequest;
import com.travelmate.hebergement.dtos.EquipementResponse;
import com.travelmate.hebergement.entities.Equipement;
import com.travelmate.hebergement.repositories.EquipementRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/accommodations/equipements")
public class EquipementController {

    @Autowired
    private EquipementRepository repository;

    // GET - Lister tous les équipements
    @GetMapping
    public List<EquipementResponse> getAll() {
        return repository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // POST - Créer un équipement
    @PostMapping
    public EquipementResponse create(@Valid @RequestBody EquipementRequest request) {
        Equipement equipement = new Equipement();
        equipement.setNom(request.getNom());
        equipement.setIcone(request.getIcone());
        Equipement saved = repository.save(equipement);
        return toResponse(saved);
    }

    // GET - Lire un équipement par ID
    @GetMapping("/{id}")
    public ResponseEntity<EquipementResponse> getById(@PathVariable Long id) {
        return repository.findById(id)
                .map(this::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // PUT - Mettre à jour un équipement
    @PutMapping("/{id}")
    public ResponseEntity<EquipementResponse> update(@PathVariable Long id, @Valid @RequestBody EquipementRequest request) {
        return repository.findById(id).map(existing -> {
            existing.setNom(request.getNom());
            existing.setIcone(request.getIcone());
            Equipement saved = repository.save(existing);
            return ResponseEntity.ok(toResponse(saved));
        }).orElse(ResponseEntity.notFound().build());
    }

    // DELETE - Supprimer un équipement
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    private EquipementResponse toResponse(Equipement equipement) {
        EquipementResponse response = new EquipementResponse();
        response.setId(equipement.getId());
        response.setNom(equipement.getNom());
        response.setIcone(equipement.getIcone());
        return response;
    }
}
