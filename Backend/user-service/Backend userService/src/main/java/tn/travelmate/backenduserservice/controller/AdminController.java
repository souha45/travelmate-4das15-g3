package tn.travelmate.backenduserservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import tn.travelmate.backenduserservice.dto.Dtos;
import tn.travelmate.backenduserservice.ennum.NomRole;
import tn.travelmate.backenduserservice.ennum.StatutCompte;
import tn.travelmate.backenduserservice.exception.ApiException;
import tn.travelmate.backenduserservice.security.CurrentUser;
import tn.travelmate.backenduserservice.service.UserService;

/** Gestion des comptes par l'administrateur (rôle ADMINISTRATEUR obligatoire, vérifié dans SecurityConfig). */
@RestController
@RequestMapping("/admin")
@Tag(name = "Administration", description = "L'administrateur gère les comptes et les rôles")
public class AdminController {
    private final UserService service;

    public AdminController(UserService service) {
        this.service = service;
    }

    @GetMapping("/users")
    @Operation(summary = "Lister les comptes (filtres facultatifs : role, statut)")
    public Dtos.PageResponse<Dtos.UserResponse> list(@RequestParam(required = false) NomRole role,
                                                     @RequestParam(required = false) StatutCompte statut,
                                                     @RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "20") int size) {
        return service.lister(role, statut, page, size);
    }

    @GetMapping("/users/{id}")
    @Operation(summary = "Consulter un compte")
    public Dtos.UserResponse get(@PathVariable Long id) {
        return service.consulter(id);
    }

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Créer un compte avec le rôle de son choix (compte ACTIF)")
    public Dtos.UserResponse create(@Valid @RequestBody Dtos.AdminCreateUserRequest r) {
        return service.creerParAdmin(r);
    }

    @PutMapping("/users/{id}")
    @Operation(summary = "Modifier le profil d'un compte")
    public Dtos.UserResponse update(@PathVariable Long id, @Valid @RequestBody Dtos.UpdateProfileRequest r) {
        return service.modifier(id, r);
    }

    @DeleteMapping("/users/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Supprimer un compte (pas le sien)")
    public void delete(@PathVariable Long id) {
        interdireSoiMeme(id, "supprimer");
        service.supprimer(id);
    }

    @PutMapping("/users/{id}/block")
    @Operation(summary = "Bloquer un compte (pas le sien)")
    public Dtos.UserResponse block(@PathVariable Long id) {
        interdireSoiMeme(id, "bloquer");
        return service.bloquer(id);
    }

    @PutMapping("/users/{id}/unblock")
    @Operation(summary = "Débloquer un compte")
    public Dtos.UserResponse unblock(@PathVariable Long id) {
        return service.debloquer(id);
    }

    @PutMapping("/partners/{id}/validate")
    @Operation(summary = "Valider un compte partenaire (EN_ATTENTE vers ACTIF)")
    public Dtos.UserResponse validatePartner(@PathVariable Long id) {
        return service.validerPartenaire(id);
    }

    @PostMapping("/users/{id}/roles/{role}")
    @Operation(summary = "Ajouter un rôle à un compte")
    public Dtos.UserResponse addRole(@PathVariable Long id, @PathVariable NomRole role) {
        return service.ajouterRole(id, role);
    }

    @DeleteMapping("/users/{id}/roles/{role}")
    @Operation(summary = "Retirer un rôle à un compte (au moins un rôle doit rester)")
    public Dtos.UserResponse removeRole(@PathVariable Long id, @PathVariable NomRole role) {
        if (role == NomRole.ADMINISTRATEUR) interdireSoiMeme(id, "retirer le rôle ADMINISTRATEUR de");
        return service.retirerRole(id, role);
    }

    private void interdireSoiMeme(Long id, String action) {
        if (id.equals(CurrentUser.id())) {
            throw ApiException.forbidden("Un administrateur ne peut pas " + action + " son propre compte");
        }
    }
}
