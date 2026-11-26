package tn.travelmate.backenduserservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import tn.travelmate.backenduserservice.dto.Dtos;
import tn.travelmate.backenduserservice.service.UserService;

/**
 * Appels entre services (OpenFeign). Cette route ne doit PAS être exposée par la passerelle.
 */
@RestController
@RequestMapping("/internal/users")
@Tag(name = "Interne", description = "Appels entre microservices")
public class InternalController {
    private final UserService service;

    public InternalController(UserService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Identité et statut d'un utilisateur (pour Vols, Hébergement, Transport)")
    public Dtos.UserIdentity identity(@PathVariable Long id) {
        return service.identite(id);
    }
}
