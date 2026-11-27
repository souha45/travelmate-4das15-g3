package com.travelmate.destination_service.controller;

import com.travelmate.destination_service.dto.ActiviteRequest;
import com.travelmate.destination_service.dto.AvisRequest;
import com.travelmate.destination_service.model.Activite;
import com.travelmate.destination_service.model.Avis;
import com.travelmate.destination_service.security.Acces;
import com.travelmate.destination_service.security.Pagination;
import com.travelmate.destination_service.service.ActiviteService;
import com.travelmate.destination_service.service.AvisService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/activites")
@RequiredArgsConstructor
public class ActiviteController {

    private final ActiviteService activiteService;
    private final AvisService avisService;

    @GetMapping("/{id}")
    public Activite obtenir(@PathVariable String id) {
        return activiteService.obtenir(id);
    }

    @GetMapping("/{id}/avis")
    public Page<Avis> avis(@PathVariable String id,
                           @RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "10") int size) {
        return avisService.lister(id, Pagination.de(page, size, Sort.by("dateCreation").descending()));
    }

    @PostMapping("/{id}/avis")
    @ResponseStatus(HttpStatus.CREATED)
    public Avis noter(@PathVariable String id,
                      @Valid @RequestBody AvisRequest requete, HttpServletRequest http) {
        return avisService.ajouter(id, Acces.connecte(http), requete);
    }

    @PutMapping("/{id}")
    public Activite modifier(@PathVariable String id,
                             @Valid @RequestBody ActiviteRequest requete, HttpServletRequest http) {
        Acces.admin(http);
        return activiteService.modifier(id, requete);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void supprimer(@PathVariable String id, HttpServletRequest http) {
        Acces.admin(http);
        activiteService.supprimer(id);
    }
}
