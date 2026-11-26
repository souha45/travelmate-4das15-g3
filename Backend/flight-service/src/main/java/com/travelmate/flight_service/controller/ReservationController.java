package com.travelmate.flight_service.controller;

import com.travelmate.flight_service.dto.*;
import com.travelmate.flight_service.enums.StatutReservation;
import com.travelmate.flight_service.service.ReservationService;
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
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
@Tag(name = "Réservations", description = "Réservation de vols et gestion des passagers")
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    @Operation(summary = "Créer une réservation",
            description = "Statut EN_ATTENTE, expire après 30 minutes sans confirmation. "
                    + "Les places du vol sont réservées immédiatement.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Réservation créée"),
            @ApiResponse(responseCode = "400", description = "Données invalides ou doublons dans la requête"),
            @ApiResponse(responseCode = "404", description = "Vol introuvable"),
            @ApiResponse(responseCode = "409", description = "Vol non réservable, places insuffisantes, siège ou passeport déjà utilisé, conflit de concurrence")
    })
    public ResponseEntity<ReservationResponse> creer(@Valid @RequestBody ReservationRequest request) {
        ReservationResponse created = reservationService.creer(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping
    @Operation(summary = "Lister les réservations (filtres optionnels)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste retournée"),
            @ApiResponse(responseCode = "400", description = "Valeur de filtre invalide")
    })
    public ResponseEntity<List<ReservationResponse>> lireToutes(
            @Parameter(description = "Filtrer par utilisateur") @RequestParam(required = false) Long userId,
            @Parameter(description = "Filtrer par vol") @RequestParam(required = false) Long volId,
            @Parameter(description = "Filtrer par statut") @RequestParam(required = false) StatutReservation statut) {
        return ResponseEntity.ok(reservationService.lireToutes(userId, volId, statut));
    }

    @GetMapping("/reference/{reference}")
    @Operation(summary = "Consulter une réservation par sa référence")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Réservation trouvée"),
            @ApiResponse(responseCode = "404", description = "Réservation introuvable")
    })
    public ResponseEntity<ReservationResponse> lireParReference(
            @Parameter(example = "RES-20261126-A1B2C3") @PathVariable String reference) {
        return ResponseEntity.ok(reservationService.lireParReference(reference));
    }

    @GetMapping("/vol/{volId}/sieges-occupes")
    @Operation(summary = "Lister les sièges occupés d'un vol",
            description = "Ne tient compte que des réservations en attente ou confirmées")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste des sièges (éventuellement vide)"),
            @ApiResponse(responseCode = "404", description = "Vol introuvable")
    })
    public ResponseEntity<List<String>> lireSiegesOccupes(@PathVariable Long volId) {
        return ResponseEntity.ok(reservationService.lireSiegesOccupes(volId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulter une réservation par son id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Réservation trouvée"),
            @ApiResponse(responseCode = "404", description = "Réservation introuvable")
    })
    public ResponseEntity<ReservationResponse> lireParId(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.lireParId(id));
    }

    @GetMapping("/{id}/passagers")
    @Operation(summary = "Lister les passagers d'une réservation")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Passagers retournés"),
            @ApiResponse(responseCode = "404", description = "Réservation introuvable")
    })
    public ResponseEntity<List<PassagerResponse>> lirePassagers(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.lirePassagers(id));
    }

    @PutMapping("/{id}/passagers/{passagerId}")
    @Operation(summary = "Modifier un passager (informations ou siège)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Passager modifié"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "404", description = "Réservation ou passager introuvable"),
            @ApiResponse(responseCode = "409", description = "Siège ou passeport déjà utilisé, réservation inactive, vol parti")
    })
    public ResponseEntity<PassagerResponse> modifierPassager(@PathVariable Long id,
                                                             @PathVariable Long passagerId,
                                                             @Valid @RequestBody PassagerRequest request) {
        return ResponseEntity.ok(reservationService.modifierPassager(id, passagerId, request));
    }

    @PutMapping("/{id}/confirmer")
    @Operation(summary = "Confirmer une réservation après paiement")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Réservation confirmée"),
            @ApiResponse(responseCode = "400", description = "paiementId manquant"),
            @ApiResponse(responseCode = "404", description = "Réservation introuvable"),
            @ApiResponse(responseCode = "409", description = "Réservation non en attente ou expirée")
    })
    public ResponseEntity<ReservationResponse> confirmer(@PathVariable Long id,
                                                         @Valid @RequestBody ConfirmationRequest request) {
        return ResponseEntity.ok(reservationService.confirmer(id, request));
    }

    @PutMapping("/{id}/annuler")
    @Operation(summary = "Annuler une réservation", description = "Libère les places du vol")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Réservation annulée"),
            @ApiResponse(responseCode = "404", description = "Réservation introuvable"),
            @ApiResponse(responseCode = "409", description = "Réservation non annulable ou vol déjà parti")
    })
    public ResponseEntity<ReservationResponse> annuler(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.annuler(id));
    }

    @PutMapping("/{id}/expirer")
    @Operation(summary = "Forcer l'expiration d'une réservation en attente",
            description = "Normalement faite automatiquement. Libère les places du vol.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Réservation expirée"),
            @ApiResponse(responseCode = "404", description = "Réservation introuvable"),
            @ApiResponse(responseCode = "409", description = "Réservation non en attente")
    })
    public ResponseEntity<ReservationResponse> expirer(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.expirer(id));
    }
}