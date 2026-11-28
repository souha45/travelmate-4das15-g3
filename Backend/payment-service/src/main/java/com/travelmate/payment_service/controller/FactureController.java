package com.travelmate.payment_service.controller;

import com.travelmate.payment_service.entity.Facture;
import com.travelmate.payment_service.service.FactureService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/payments/invoices")
@RequiredArgsConstructor
public class FactureController {
    private final FactureService service;

    @GetMapping
    public List<Facture> lister(@RequestParam(required = false) Long utilisateurId) {
        return service.rechercher(utilisateurId);
    }

    @GetMapping("/{id}")
    public Facture getById(@PathVariable Long id) {
        return service.trouver(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Facture creer(@Valid @RequestBody Facture facture) {
        return service.creer(facture);
    }

    @PutMapping("/{id}")
    public Facture modifier(@PathVariable Long id, @Valid @RequestBody Facture facture) {
        return service.modifier(id, facture);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void supprimer(@PathVariable Long id) {
        service.supprimer(id);
    }
}