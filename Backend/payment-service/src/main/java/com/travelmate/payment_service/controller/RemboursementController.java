package com.travelmate.payment_service.controller;

import com.travelmate.payment_service.entity.Remboursement;
import com.travelmate.payment_service.service.RemboursementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/payments/refunds")
@RequiredArgsConstructor
public class RemboursementController {
    private final RemboursementService service;

    @GetMapping
    public List<Remboursement> lister(@RequestParam(required = false) Long paiementId) {
        return service.rechercher(paiementId);
    }

    @GetMapping("/{id}")
    public Remboursement getById(@PathVariable Long id) {
        return service.trouver(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Remboursement creer(@Valid @RequestBody Remboursement remboursement) {
        return service.creer(remboursement);
    }

    @PutMapping("/{id}")
    public Remboursement modifier(@PathVariable Long id, @Valid @RequestBody Remboursement remboursement) {
        return service.modifier(id, remboursement);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void supprimer(@PathVariable Long id) {
        service.supprimer(id);
    }
}