package tn.travelmate.backenduserservice.controller;



import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import tn.travelmate.backenduserservice.dto.Dtos;
import tn.travelmate.backenduserservice.service.UserService;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentification", description = "Inscription et connexion communes à tous les utilisateurs")
public class AuthController {
    private final UserService service;

    public AuthController(UserService service) {
        this.service = service;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "S'inscrire (VOYAGEUR par défaut, ou PARTENAIRE avec nomSociete et typePartenaire)")
    public Dtos.UserResponse register(@Valid @RequestBody Dtos.RegisterRequest r) {
        return service.inscrire(r);
    }

    @PostMapping("/login")
    @Operation(summary = "Se connecter (tous les rôles) : renvoie le jeton JWT")
    public Dtos.AuthResponse login(@Valid @RequestBody Dtos.LoginRequest r) {
        return service.connecter(r);
    }
}