package com.travelmate.hebergement.controllers;

import com.travelmate.hebergement.dtos.ChambreMapper;
import com.travelmate.hebergement.dtos.ChambreRequest;
import com.travelmate.hebergement.dtos.ChambreResponse;
import com.travelmate.hebergement.entities.Chambre;
import com.travelmate.hebergement.repositories.ChambreRepository;
import com.travelmate.hebergement.repositories.HebergementRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/accommodations")
public class ChambreController {

    @Autowired
    private ChambreRepository chambreRepository;

    @Autowired
    private HebergementRepository hebergementRepository;

    @Autowired
    private ChambreMapper mapper;

    // 1. Lire toutes les chambres d'un hébergement
    @GetMapping("/{hebergementId}/chambres")
    public ResponseEntity<List<ChambreResponse>> getChambresByHebergement(@PathVariable Long hebergementId) {
        if (!hebergementRepository.existsById(hebergementId)) {
            return ResponseEntity.notFound().build();
        }
        List<Chambre> chambres = chambreRepository.findAll().stream()
                .filter(c -> c.getHebergement().getId().equals(hebergementId))
                .toList();
        List<ChambreResponse> responses = chambres.stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    // 2. Créer une chambre pour un hébergement spécifique
    @PostMapping("/{hebergementId}/chambres")
    public ResponseEntity<ChambreResponse> createChambre(@PathVariable Long hebergementId, @Valid @RequestBody ChambreRequest request) {
        return hebergementRepository.findById(hebergementId).map(hebergement -> {
            Chambre chambre = mapper.toEntity(request);
            chambre.setHebergement(hebergement);
            Chambre saved = chambreRepository.save(chambre);
            return ResponseEntity.ok(mapper.toResponse(saved));
        }).orElse(ResponseEntity.notFound().build());
    }

    // 3. Lire une chambre spécifique (Route dédiée pour éviter les conflits)
    @GetMapping("/chambres/{id}")
    public ResponseEntity<ChambreResponse> getChambreById(@PathVariable Long id) {
        return chambreRepository.findById(id)
                .map(mapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 4. Mettre à jour une chambre
    @PutMapping("/chambres/{id}")
    public ResponseEntity<ChambreResponse> updateChambre(@PathVariable Long id, @Valid @RequestBody ChambreRequest request) {
        return chambreRepository.findById(id).map(chambre -> {
            chambre.setType(request.getType());
            chambre.setCapacite(request.getCapacite());
            chambre.setPrixParNuit(request.getPrixParNuit());
            chambre.setNombreTotal(request.getNombreTotal());
            chambre.setDescription(request.getDescription());
            Chambre saved = chambreRepository.save(chambre);
            return ResponseEntity.ok(mapper.toResponse(saved));
        }).orElse(ResponseEntity.notFound().build());
    }

    // 5. Supprimer une chambre
    @DeleteMapping("/chambres/{id}")
    public ResponseEntity<Void> deleteChambre(@PathVariable Long id) {
        if (chambreRepository.existsById(id)) {
            chambreRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}