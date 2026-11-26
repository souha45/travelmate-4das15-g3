package tn.travelmate.backenduserservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import tn.travelmate.backenduserservice.dto.Dtos;
import tn.travelmate.backenduserservice.security.CurrentUser;
import tn.travelmate.backenduserservice.service.PreferenceService;

@RestController
@RequestMapping("/users/{userId}/preferences")
@Tag(name = "Préférences de voyage", description = "CRUD des préférences d'un utilisateur")
public class PreferenceController {
    private final PreferenceService service;

    public PreferenceController(PreferenceService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Définir les préférences (409 si elles existent déjà)")
    public Dtos.PreferenceResponse create(@PathVariable Long userId, @Valid @RequestBody Dtos.PreferenceRequest r) {
        CurrentUser.requireOwnerOrAdmin(userId);
        return service.creer(userId, r);
    }

    @GetMapping
    @Operation(summary = "Consulter les préférences")
    public Dtos.PreferenceResponse get(@PathVariable Long userId) {
        CurrentUser.requireOwnerOrAdmin(userId);
        return service.consulter(userId);
    }

    @PutMapping
    @Operation(summary = "Modifier les préférences")
    public Dtos.PreferenceResponse update(@PathVariable Long userId, @Valid @RequestBody Dtos.PreferenceRequest r) {
        CurrentUser.requireOwnerOrAdmin(userId);
        return service.modifier(userId, r);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Supprimer les préférences")
    public void delete(@PathVariable Long userId) {
        CurrentUser.requireOwnerOrAdmin(userId);
        service.supprimer(userId);
    }
}
