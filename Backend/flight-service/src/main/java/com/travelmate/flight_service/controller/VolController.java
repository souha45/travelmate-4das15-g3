package com.travelmate.flight_service.controller;

import com.travelmate.flight_service.dto.RetardRequest;
import com.travelmate.flight_service.dto.VolRequest;
import com.travelmate.flight_service.dto.VolResponse;
import com.travelmate.flight_service.enums.StatutModeration;
import com.travelmate.flight_service.enums.StatutVol;
import com.travelmate.flight_service.service.VolService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/vols")
@RequiredArgsConstructor
@Tag(name = "Vols", description = "Gestion, recherche et modération des vols")
public class VolController {

    private final VolService volService;

    @PostMapping
    @Operation(summary = "Créer un vol",
            description = "Le vol est créé avec le statut PROGRAMME et la modération EN_REVISION")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Vol créé"),
            @ApiResponse(responseCode = "400", description = "Données invalides ou incohérentes"),
            @ApiResponse(responseCode = "404", description = "Compagnie ou aéroport introuvable"),
            @ApiResponse(responseCode = "409", description = "Numéro de vol déjà utilisé")
    })
    public ResponseEntity<VolResponse> creer(@Valid @RequestBody VolRequest request) {
        VolResponse created = volService.creer(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping
    @Operation(summary = "Lister les vols (filtres optionnels)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste retournée"),
            @ApiResponse(responseCode = "400", description = "Valeur de filtre invalide")
    })
    public ResponseEntity<List<VolResponse>> lireTous(
            @Parameter(description = "Filtrer par compagnie") @RequestParam(required = false) Long compagnieId,
            @Parameter(description = "Filtrer par statut du vol") @RequestParam(required = false) StatutVol statut,
            @Parameter(description = "Filtrer par statut de modération") @RequestParam(required = false) StatutModeration moderation) {
        return ResponseEntity.ok(volService.lireTous(compagnieId, statut, moderation));
    }

    @GetMapping("/recherche")
    @Operation(summary = "Recherche publique de vols",
            description = "Retourne les vols approuvés, programmés ou retardés, avec assez de places, "
                    + "le jour demandé, triés par heure de départ")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Résultats (liste éventuellement vide)"),
            @ApiResponse(responseCode = "400", description = "Paramètre manquant ou invalide")
    })
    public ResponseEntity<List<VolResponse>> rechercher(
            @Parameter(description = "Code IATA de départ", example = "TUN") @RequestParam String depart,
            @Parameter(description = "Code IATA d'arrivée", example = "CDG") @RequestParam String arrivee,
            @Parameter(description = "Date de départ (yyyy-MM-dd)", example = "2027-03-10")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @Parameter(description = "Nombre de passagers", example = "2")
            @RequestParam(defaultValue = "1") int passagers) {
        return ResponseEntity.ok(volService.rechercher(depart, arrivee, date, passagers));
    }

    @GetMapping("/numero/{numero}")
    @Operation(summary = "Consulter un vol par son numéro")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Vol trouvé"),
            @ApiResponse(responseCode = "404", description = "Vol introuvable")
    })
    public ResponseEntity<VolResponse> lireParNumero(
            @Parameter(example = "TU750") @PathVariable String numero) {
        return ResponseEntity.ok(volService.lireParNumero(numero));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulter un vol par son id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Vol trouvé"),
            @ApiResponse(responseCode = "404", description = "Vol introuvable")
    })
    public ResponseEntity<VolResponse> lireParId(@PathVariable Long id) {
        return ResponseEntity.ok(volService.lireParId(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier un vol")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Vol modifié"),
            @ApiResponse(responseCode = "400", description = "Données invalides ou incohérentes"),
            @ApiResponse(responseCode = "404", description = "Vol, compagnie ou aéroport introuvable"),
            @ApiResponse(responseCode = "409", description = "Numéro déjà utilisé, vol annulé/terminé, ou places déjà réservées")
    })
    public ResponseEntity<VolResponse> modifier(@PathVariable Long id,
                                                @Valid @RequestBody VolRequest request) {
        return ResponseEntity.ok(volService.modifier(id, request));
    }

    @PutMapping("/{id}/approuver")
    @Operation(summary = "Approuver un vol (modération)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Vol approuvé"),
            @ApiResponse(responseCode = "404", description = "Vol introuvable"),
            @ApiResponse(responseCode = "409", description = "Vol déjà approuvé")
    })
    public ResponseEntity<VolResponse> approuver(@PathVariable Long id) {
        return ResponseEntity.ok(volService.approuver(id));
    }

    @PutMapping("/{id}/suspendre")
    @Operation(summary = "Suspendre un vol (modération)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Vol suspendu"),
            @ApiResponse(responseCode = "404", description = "Vol introuvable"),
            @ApiResponse(responseCode = "409", description = "Vol déjà suspendu")
    })
    public ResponseEntity<VolResponse> suspendre(@PathVariable Long id) {
        return ResponseEntity.ok(volService.suspendre(id));
    }

    @PutMapping("/{id}/retarder")
    @Operation(summary = "Retarder un vol", description = "Décale les horaires et passe le statut à RETARD")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Vol retardé"),
            @ApiResponse(responseCode = "400", description = "Horaires invalides"),
            @ApiResponse(responseCode = "404", description = "Vol introuvable"),
            @ApiResponse(responseCode = "409", description = "Le vol ne peut pas être retardé dans son état actuel")
    })
    public ResponseEntity<VolResponse> retarder(@PathVariable Long id,
                                                @Valid @RequestBody RetardRequest request) {
        return ResponseEntity.ok(volService.retarder(id, request));
    }

    @PutMapping("/{id}/annuler")
    @Operation(summary = "Annuler un vol")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Vol annulé"),
            @ApiResponse(responseCode = "404", description = "Vol introuvable"),
            @ApiResponse(responseCode = "409", description = "Vol déjà annulé ou terminé")
    })
    public ResponseEntity<VolResponse> annuler(@PathVariable Long id) {
        return ResponseEntity.ok(volService.annuler(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un vol")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Vol supprimé"),
            @ApiResponse(responseCode = "404", description = "Vol introuvable"),
            @ApiResponse(responseCode = "409", description = "Des réservations sont associées à ce vol")
    })
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        volService.supprimer(id);
        return ResponseEntity.noContent().build();
    }
}