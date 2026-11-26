package tn.travelmate.backenduserservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import tn.travelmate.backenduserservice.dto.Dtos;
import tn.travelmate.backenduserservice.security.CurrentUser;
import tn.travelmate.backenduserservice.service.PartnerProfileService;

@RestController
@RequestMapping("/users/{userId}/partner-profile")
@Tag(name = "Profil partenaire", description = "CRUD du profil d'un compte PARTENAIRE")
public class PartnerProfileController {
    private final PartnerProfileService service;

    public PartnerProfileController(PartnerProfileService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Créer le profil partenaire (409 s'il existe déjà)")
    public Dtos.PartenaireResponse create(@PathVariable Long userId, @Valid @RequestBody Dtos.PartnerProfileRequest r) {
        CurrentUser.requireOwnerOrAdmin(userId);
        return service.creer(userId, r);
    }

    @GetMapping
    @Operation(summary = "Consulter le profil partenaire")
    public Dtos.PartenaireResponse get(@PathVariable Long userId) {
        CurrentUser.requireOwnerOrAdmin(userId);
        return service.consulter(userId);
    }

    @PutMapping
    @Operation(summary = "Modifier le profil partenaire")
    public Dtos.PartenaireResponse update(@PathVariable Long userId, @Valid @RequestBody Dtos.PartnerProfileRequest r) {
        CurrentUser.requireOwnerOrAdmin(userId);
        return service.modifier(userId, r);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Supprimer le profil partenaire")
    public void delete(@PathVariable Long userId) {
        CurrentUser.requireOwnerOrAdmin(userId);
        service.supprimer(userId);
    }
}
