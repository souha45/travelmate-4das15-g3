package com.travelmate.destination_service.controller;

import com.travelmate.destination_service.dto.FavoriRequest;
import com.travelmate.destination_service.model.Destination;
import com.travelmate.destination_service.model.Favori;
import com.travelmate.destination_service.security.Acces;
import com.travelmate.destination_service.service.FavoriService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favoris")
@RequiredArgsConstructor
public class FavoriController {

    private final FavoriService favoriService;

    @GetMapping
    public List<Destination> lister(HttpServletRequest http) {
        return favoriService.lister(Acces.connecte(http));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Favori ajouter(@Valid @RequestBody FavoriRequest requete, HttpServletRequest http) {
        return favoriService.ajouter(Acces.connecte(http), requete.destinationId());
    }

    @DeleteMapping("/{destinationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void retirer(@PathVariable String destinationId, HttpServletRequest http) {
        favoriService.retirer(Acces.connecte(http), destinationId);
    }
}
