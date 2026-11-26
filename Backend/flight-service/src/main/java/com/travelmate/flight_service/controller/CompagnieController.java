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
@Tag(name = "Compagnies", description = "Gestion des compagnies aériennes")
public class CompagnieController {

    private final CompagnieService compagnieService;

    @PostMapping
    @Operation(summary = "Créer une compagnie")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Compagnie créée"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "409", description = "Code déjà utilisé")
    })
    public ResponseEntity<CompagnieResponse> creer(@Valid @RequestBody CompagnieRequest request) {
        CompagnieResponse created = compagnieService.creer(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);   // 201 + header Location
    }

    @GetMapping
    @Operation(summary = "Lister les compagnies (filtre optionnel par pays)")
    @ApiResponse(responseCode = "200", description = "Liste retournée")
    public ResponseEntity<List<CompagnieResponse>> lireToutes(
            @RequestParam(required = false) String pays) {
        return ResponseEntity.ok(compagnieService.lireToutes(pays));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulter une compagnie par son id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Compagnie trouvée"),
            @ApiResponse(responseCode = "404", description = "Compagnie introuvable")
    })
    public ResponseEntity<CompagnieResponse> lireParId(@PathVariable Long id) {
        return ResponseEntity.ok(compagnieService.lireParId(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier une compagnie")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Compagnie modifiée"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "404", description = "Compagnie introuvable"),
            @ApiResponse(responseCode = "409", description = "Code déjà utilisé")
    })
    public ResponseEntity<CompagnieResponse> modifier(@PathVariable Long id,
                                                      @Valid @RequestBody CompagnieRequest request) {
        return ResponseEntity.ok(compagnieService.modifier(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une compagnie")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Compagnie supprimée"),
            @ApiResponse(responseCode = "404", description = "Compagnie introuvable"),
            @ApiResponse(responseCode = "409", description = "Des vols sont associés à cette compagnie")
    })
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        compagnieService.supprimer(id);
        return ResponseEntity.noContent().build();
    }
}