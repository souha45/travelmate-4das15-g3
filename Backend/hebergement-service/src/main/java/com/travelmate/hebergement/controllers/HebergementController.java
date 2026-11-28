package com.travelmate.hebergement.controllers;

import com.travelmate.hebergement.dtos.EquipementResponse;
import com.travelmate.hebergement.dtos.HebergementMapper;
import com.travelmate.hebergement.dtos.HebergementRequest;
import com.travelmate.hebergement.dtos.HebergementResponse;
import com.travelmate.hebergement.entities.Equipement;
import com.travelmate.hebergement.entities.Hebergement;
import com.travelmate.hebergement.repositories.EquipementRepository;
import com.travelmate.hebergement.repositories.HebergementRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/accommodations")
public class HebergementController {

    @Autowired
    private HebergementRepository repository;

    @Autowired
    private HebergementMapper mapper;

    @Autowired
    private EquipementRepository equipementRepository;

    // 1. LIRE TOUT (renvoie une liste de DTO Response)
    @GetMapping
    public List<HebergementResponse> getAll() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    // 2. CRÉER (reçoit un DTO Request, renvoie un DTO Response)
    @PostMapping
    public HebergementResponse create(@Valid @RequestBody HebergementRequest request) {
        Hebergement hebergement = mapper.toEntity(request);
        Hebergement saved = repository.save(hebergement);
        return mapper.toResponse(saved);
    }

    // 3. LIRE UN SEUL (renvoie un DTO Response)
    @GetMapping("/{id}")
    public ResponseEntity<HebergementResponse> getById(@PathVariable Long id) {
        return repository.findById(id)
                .map(entity -> ResponseEntity.ok(mapper.toResponse(entity)))
                .orElse(ResponseEntity.notFound().build());
    }

    // 4. METTRE À JOUR
    @PutMapping("/{id}")
    public ResponseEntity<HebergementResponse> update(@PathVariable Long id, @Valid @RequestBody HebergementRequest request) {
        return repository.findById(id).map(existing -> {
            existing.setNom(request.getNom());
            existing.setType(request.getType());
            existing.setDescription(request.getDescription());
            existing.setAdresse(request.getAdresse());
            existing.setVille(request.getVille());
            existing.setPays(request.getPays());
            existing.setLatitude(request.getLatitude());
            existing.setLongitude(request.getLongitude());
            existing.setEtoiles(request.getEtoiles());
            existing.setPartenaireId(request.getPartenaireId());

            Hebergement saved = repository.save(existing);
            return ResponseEntity.ok(mapper.toResponse(saved));
        }).orElse(ResponseEntity.notFound().build());
    }

    // 5. SUPPRIMER (inchangé)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    // 6. Ajouter un équipement à un hébergement
    @PostMapping("/{hebergementId}/equipements/{equipementId}")
    public ResponseEntity<HebergementResponse> addEquipement(@PathVariable Long hebergementId, @PathVariable Long equipementId) {
        return repository.findById(hebergementId).map(hebergement -> {
            Equipement equipement = equipementRepository.findById(equipementId)
                    .orElseThrow(() -> new RuntimeException("Equipement not found"));
            hebergement.getEquipements().add(equipement);
            repository.save(hebergement);
            return ResponseEntity.ok(mapper.toResponse(hebergement));
        }).orElse(ResponseEntity.notFound().build());
    }

    // 7. Retirer un équipement d'un hébergement
    @DeleteMapping("/{hebergementId}/equipements/{equipementId}")
    public ResponseEntity<HebergementResponse> removeEquipement(@PathVariable Long hebergementId, @PathVariable Long equipementId) {
        return repository.findById(hebergementId).map(hebergement -> {
            Equipement equipement = equipementRepository.findById(equipementId)
                    .orElseThrow(() -> new RuntimeException("Equipement not found"));
            hebergement.getEquipements().remove(equipement);
            repository.save(hebergement);
            return ResponseEntity.ok(mapper.toResponse(hebergement));
        }).orElse(ResponseEntity.notFound().build());
    }

    // 8. Lister les équipements d'un hébergement
    @GetMapping("/{hebergementId}/equipements")
    public ResponseEntity<Set<EquipementResponse>> getEquipements(@PathVariable Long hebergementId) {
        return repository.findById(hebergementId)
                .map(hebergement -> {
                    Set<EquipementResponse> responses = hebergement.getEquipements().stream()
                            .map(this::toEquipementResponse)
                            .collect(Collectors.toSet());
                    return ResponseEntity.ok(responses);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    private EquipementResponse toEquipementResponse(Equipement equipement) {
        EquipementResponse response = new EquipementResponse();
        response.setId(equipement.getId());
        response.setNom(equipement.getNom());
        response.setIcone(equipement.getIcone());
        return response;
    }
}