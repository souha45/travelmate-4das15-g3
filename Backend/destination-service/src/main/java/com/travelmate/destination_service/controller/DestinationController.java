package com.travelmate.destination_service.controller;

import com.travelmate.destination_service.dto.*;
import com.travelmate.destination_service.model.*;
import com.travelmate.destination_service.enums.*;
import com.travelmate.destination_service.security.Acces;
import com.travelmate.destination_service.security.Pagination;
import com.travelmate.destination_service.service.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/destinations")
@RequiredArgsConstructor
public class DestinationController {

    private final DestinationService destinationService;
    private final ActiviteService activiteService;
    private final MeteoService meteoService;

    @GetMapping
    public Page<Destination> rechercher(@RequestParam(required = false) String pays,
                                        @RequestParam(required = false) TypeDestination type,
                                        @RequestParam(required = false) String recherche,
                                        @RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "9") int size) {
        return destinationService.rechercher(pays, type, recherche,
                Pagination.de(page, size, Sort.by("nom")));
    }

    @GetMapping("/pays")
    public List<String> pays() {
        return destinationService.pays();
    }

    @GetMapping("/{id}")
    public Destination obtenir(@PathVariable String id) {
        return destinationService.obtenirActive(id);
    }

    @GetMapping("/{id}/meteo")
    public MeteoReponse meteo(@PathVariable String id) {
        return meteoService.obtenir(id);
    }

    @GetMapping("/{id}/activites")
    public Page<Activite> activites(@PathVariable String id,
                                    @RequestParam(required = false) Categorie categorie,
                                    @RequestParam(required = false) Double prixMax,
                                    @RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "9") int size) {
        return activiteService.lister(id, categorie, prixMax,
                Pagination.de(page, size, Sort.by("nom")));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Destination creer(@Valid @RequestBody DestinationRequest requete, HttpServletRequest http) {
        Acces.admin(http);
        return destinationService.creer(requete);
    }

    @PutMapping("/{id}")
    public Destination modifier(@PathVariable String id,
                                @Valid @RequestBody DestinationRequest requete, HttpServletRequest http) {
        Acces.admin(http);
        return destinationService.modifier(id, requete);
    }

    @PatchMapping("/{id}/statut")
    public Destination changerStatut(@PathVariable String id,
                                     @Valid @RequestBody StatutRequest requete, HttpServletRequest http) {
        Acces.admin(http);
        return destinationService.changerStatut(id, requete.actif());
    }

    @PostMapping("/{id}/activites")
    @ResponseStatus(HttpStatus.CREATED)
    public Activite creerActivite(@PathVariable String id,
                                  @Valid @RequestBody ActiviteRequest requete, HttpServletRequest http) {
        Acces.admin(http);
        return activiteService.creer(id, requete);
    }
}
