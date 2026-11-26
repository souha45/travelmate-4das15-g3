package tn.travelmate.backenduserservice.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import tn.travelmate.backenduserservice.dto.Dtos;
import tn.travelmate.backenduserservice.security.CurrentUser;
import tn.travelmate.backenduserservice.service.UserService;

@RestController
@RequestMapping("/users")
@Tag(name = "Utilisateurs", description = "Mon compte : le propriétaire ou un administrateur")
public class UserController {
    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping("/me")
    @Operation(summary = "Consulter mon compte (identifié par le jeton)")
    public Dtos.UserResponse me() {
        return service.consulter(CurrentUser.id());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulter un compte (propriétaire ou admin)")
    public Dtos.UserResponse get(@PathVariable Long id) {
        CurrentUser.requireOwnerOrAdmin(id);
        return service.consulter(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier le profil (propriétaire ou admin)")
    public Dtos.UserResponse update(@PathVariable Long id, @Valid @RequestBody Dtos.UpdateProfileRequest r) {
        CurrentUser.requireOwnerOrAdmin(id);
        return service.modifier(id, r);
    }

    @PutMapping("/{id}/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Changer mon mot de passe (propriétaire uniquement)")
    public void password(@PathVariable Long id, @Valid @RequestBody Dtos.ChangePasswordRequest r) {
        CurrentUser.requireOwner(id);
        service.changerMotDePasse(id, r);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Supprimer un compte (propriétaire ou admin)")
    public void delete(@PathVariable Long id) {
        CurrentUser.requireOwnerOrAdmin(id);
        service.supprimer(id);
    }
}
