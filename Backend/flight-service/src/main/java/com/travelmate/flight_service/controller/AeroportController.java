package com.travelmate.flight_service.controller;

import com.travelmate.flight_service.dto.AeroportRequest;
import com.travelmate.flight_service.dto.AeroportResponse;
import com.travelmate.flight_service.service.AeroportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@RequestMapping("/api/aeroports")
@RequiredArgsConstructor
@Tag(name = "Aéroports", description = "Gestion des aéroports")
public class AeroportController {

    private final AeroportService aeroportService;

    @PostMapping
    @Operation(summary = "Créer un aéroport")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Aéroport créé"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "409", description = "Code IATA déjà utilisé")
    })
    public ResponseEntity<AeroportResponse> creer(@Valid @RequestBody AeroportRequest request) {
        AeroportResponse created = aeroportService.creer(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping
    @Operation(summary = "Lister les aéroports (filtres optionnels par ville et/ou pays)")
    @ApiResponse(responseCode = "200", description = "Liste retournée")
    public ResponseEntity<List<AeroportResponse>> lireTous(
            @Parameter(description = "Filtrer par ville", example = "Tunis")
            @RequestParam(required = false) String ville,
            @Parameter(description = "Filtrer par pays", example = "Tunisie")
            @RequestParam(required = false) String pays) {
        return ResponseEntity.ok(aeroportService.lireTous(ville, pays));
    }

    @GetMapping("/recherche")
    @Operation(summary = "Rechercher par mot-clé (code, nom ou ville)",
            description = "Utile pour l'autocomplétion : q=tun retourne Tunis-Carthage")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Résultats (liste éventuellement vide)"),
            @ApiResponse(responseCode = "409", description = "Paramètre q manquant")
    })
    public ResponseEntity<List<AeroportResponse>> rechercher(
            @Parameter(description = "Mot-clé", example = "tun") @RequestParam String q) {
        return ResponseEntity.ok(aeroportService.rechercher(q));
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "Consulter un aéroport par son code IATA")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Aéroport trouvé"),
            @ApiResponse(responseCode = "404", description = "Aéroport introuvable")
    })
    public ResponseEntity<AeroportResponse> lireParCode(
            @Parameter(example = "TUN") @PathVariable String code) {
        return ResponseEntity.ok(aeroportService.lireParCode(code));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulter un aéroport par son id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Aéroport trouvé"),
            @ApiResponse(responseCode = "404", description = "Aéroport introuvable")
    })
    public ResponseEntity<AeroportResponse> lireParId(@PathVariable Long id) {
        return ResponseEntity.ok(aeroportService.lireParId(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier un aéroport")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Aéroport modifié"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "404", description = "Aéroport introuvable"),
            @ApiResponse(responseCode = "409", description = "Code IATA déjà utilisé")
    })
    public ResponseEntity<AeroportResponse> modifier(@PathVariable Long id,
                                                     @Valid @RequestBody AeroportRequest request) {
        return ResponseEntity.ok(aeroportService.modifier(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un aéroport")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Aéroport supprimé"),
            @ApiResponse(responseCode = "404", description = "Aéroport introuvable"),
            @ApiResponse(responseCode = "409", description = "Des vols sont associés à cet aéroport")
    })
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        aeroportService.supprimer(id);
        return ResponseEntity.noContent().build();
    }
}