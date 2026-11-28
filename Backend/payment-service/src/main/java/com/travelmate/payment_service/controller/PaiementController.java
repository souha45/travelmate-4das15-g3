package com.travelmate.payment_service.controller;

import com.travelmate.payment_service.entity.Paiement;
import com.travelmate.payment_service.enums.StatutPaiement;
import com.travelmate.payment_service.service.PaiementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaiementController {
    private final PaiementService service;

    @GetMapping
    public List<Paiement> lister(@RequestParam(required = false) Long utilisateurId, @RequestParam(required = false) StatutPaiement statut) {
        return service.rechercher(utilisateurId, statut);
    }

    @GetMapping("/{id}")
    public Paiement getById(@PathVariable Long id) {
        return service.trouver(id);
    }

    @GetMapping("/reference/{reference}")
    public Paiement getByReference(@PathVariable String reference) {
        return service.trouverParReference(reference);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Paiement creer(@Valid @RequestBody Paiement paiement) {
        return service.creer(paiement);
    }

    @PutMapping("/{id}")
    public Paiement modifier(@PathVariable Long id, @Valid @RequestBody Paiement paiement) {
        return service.modifier(id, paiement);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void supprimer(@PathVariable Long id) {
        service.supprimer(id);
    }
}