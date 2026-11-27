package com.travelmate.destination_service.controller;

import com.travelmate.destination_service.dto.AvisRequest;
import com.travelmate.destination_service.model.Avis;
import com.travelmate.destination_service.security.Acces;
import com.travelmate.destination_service.service.AvisService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/avis")
@RequiredArgsConstructor
public class AvisController {

    private final AvisService avisService;

    @PutMapping("/{id}")
    public Avis modifier(@PathVariable String id,
                         @Valid @RequestBody AvisRequest requete, HttpServletRequest http) {
        return avisService.modifier(id, Acces.connecte(http), requete);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void supprimer(@PathVariable String id, HttpServletRequest http) {
        avisService.supprimer(id, Acces.connecte(http));
    }
}
