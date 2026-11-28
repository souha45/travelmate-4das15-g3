package com.travelmate.flight_service.controller;

import com.travelmate.flight_service.dto.CompagnieRequest;
import com.travelmate.flight_service.dto.CompagnieResponse;
import com.travelmate.flight_service.service.CompagnieService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/compagnies")
@RequiredArgsConstructor
@Tag(name = "Compagnies", description = "Compagnies aériennes : chaque partenaire gère la sienne")
public class CompagnieController {

    private final CompagnieService compagnieService;

    @PostMapping
    @Operation(summary = "Créer MA compagnie (rôle PARTENAIRE)",
            description = "Le partenaireId est pris dans le JWT. Un partenaire ne peut avoir qu'une compagnie.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Compagnie créée"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Rôle insuffisant"),
            @ApiResponse(responseCode = "409", description = "Compagnie déjà créée ou code déjà utilisé")
    })
    public ResponseEntity<CompagnieResponse> creer(@Valid @RequestBody CompagnieRequest request) {
        CompagnieResponse created = compagnieService.creer(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping
    @Operation(summary = "Lister les compagnies actives (public ; l'admin voit toutes)")
    public ResponseEntity<List<CompagnieResponse>> lireToutes(@RequestParam(required = false) String pays) {
        return ResponseEntity.ok(compagnieService.lireToutes(pays));
    }

    @GetMapping("/moi")
    @Operation(summary = "Consulter MA compagnie (PARTENAIRE)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ma compagnie"),
            @ApiResponse(responseCode = "404", description = "Pas encore créée")
    })
    public ResponseEntity<CompagnieResponse> lireMaCompagnie() {
        return ResponseEntity.ok(compagnieService.lireMaCompagnie());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulter une compagnie (non active : propriétaire ou admin seulement)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Compagnie trouvée"),
            @ApiResponse(responseCode = "404", description = "Introuvable ou non visible")
    })
    public ResponseEntity<CompagnieResponse> lireParId(@PathVariable Long id) {
        return ResponseEntity.ok(compagnieService.lireParId(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier une compagnie (propriétaire ou admin)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Compagnie modifiée"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "403", description = "Ce n'est pas votre compagnie"),
            @ApiResponse(responseCode = "404", description = "Introuvable"),
            @ApiResponse(responseCode = "409", description = "Code déjà utilisé ou compagnie suspendue")
    })
    public ResponseEntity<CompagnieResponse> modifier(@PathVariable Long id,
                                                      @Valid @RequestBody CompagnieRequest request) {
        return ResponseEntity.ok(compagnieService.modifier(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Désactiver une compagnie (pas de suppression)",
            description = "Statut INACTIVE : ses vols disparaissent de la recherche. Réactivable.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Compagnie désactivée"),
            @ApiResponse(responseCode = "403", description = "Ce n'est pas votre compagnie"),
            @ApiResponse(responseCode = "404", description = "Introuvable"),
            @ApiResponse(responseCode = "409", description = "Déjà inactive ou suspendue")
    })
    public ResponseEntity<CompagnieResponse> desactiver(@PathVariable Long id) {
        return ResponseEntity.ok(compagnieService.desactiver(id));
    }

    @PutMapping("/{id}/reactiver")
    @Operation(summary = "Réactiver une compagnie inactive (propriétaire ou admin)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Compagnie réactivée"),
            @ApiResponse(responseCode = "403", description = "Ce n'est pas votre compagnie"),
            @ApiResponse(responseCode = "404", description = "Introuvable"),
            @ApiResponse(responseCode = "409", description = "Déjà active ou suspendue par l'admin")
    })
    public ResponseEntity<CompagnieResponse> reactiver(@PathVariable Long id) {
        return ResponseEntity.ok(compagnieService.reactiver(id));
    }

    @PutMapping("/{id}/suspendre")
    @Operation(summary = "Suspendre une compagnie (ADMINISTRATEUR)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Compagnie suspendue"),
            @ApiResponse(responseCode = "403", description = "Réservé à l'admin"),
            @ApiResponse(responseCode = "404", description = "Introuvable"),
            @ApiResponse(responseCode = "409", description = "Déjà suspendue")
    })
    public ResponseEntity<CompagnieResponse> suspendre(@PathVariable Long id) {
        return ResponseEntity.ok(compagnieService.suspendre(id));
    }
}