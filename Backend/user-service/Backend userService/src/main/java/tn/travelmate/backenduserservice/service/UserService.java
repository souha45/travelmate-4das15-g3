package tn.travelmate.backenduserservice.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.travelmate.backenduserservice.dto.Dtos.*;
import tn.travelmate.backenduserservice.ennum.NomRole;
import tn.travelmate.backenduserservice.ennum.StatutCompte;
import tn.travelmate.backenduserservice.entity.ProfilPartenaire;
import tn.travelmate.backenduserservice.entity.Role;
import tn.travelmate.backenduserservice.entity.Utilisateur;
import tn.travelmate.backenduserservice.exception.ApiException;
import tn.travelmate.backenduserservice.repository.RoleRepository;
import tn.travelmate.backenduserservice.repository.UtilisateurRepository;
import tn.travelmate.backenduserservice.security.JwtService;

/**
 * Logique métier du service Utilisateurs : inscription, connexion (JWT), CRUD des comptes, administration.
 * Les droits (propriétaire ou administrateur) sont vérifiés dans les contrôleurs.
 */
@Service
@Transactional
public class UserService {
    private final UtilisateurRepository users;
    private final RoleRepository roles;
    private final PasswordEncoder encoder;
    private final UserMapper mapper;
    private final JwtService jwt;

    public UserService(UtilisateurRepository users, RoleRepository roles, PasswordEncoder encoder,
                       UserMapper mapper, JwtService jwt) {
        this.users = users;
        this.roles = roles;
        this.encoder = encoder;
        this.mapper = mapper;
        this.jwt = jwt;
    }

    // ------------------------------------------------------------------ inscription / connexion (tous les rôles)
    public UserResponse inscrire(RegisterRequest r) {
        NomRole nomRole = r.role() == null ? NomRole.VOYAGEUR : r.role();
        if (nomRole == NomRole.ADMINISTRATEUR) {
            throw ApiException.forbidden("Un compte administrateur ne peut pas être créé par inscription");
        }
        verifierEmailLibre(r.email());
        Utilisateur u = new Utilisateur(r.prenom().trim(), r.nom().trim(), r.email().trim().toLowerCase(),
                encoder.encode(r.password()));
        u.ajouterRole(role(nomRole));
        if (nomRole == NomRole.PARTENAIRE) {
            if (r.nomSociete() == null || r.nomSociete().isBlank() || r.typePartenaire() == null) {
                throw ApiException.badRequest("nomSociete et typePartenaire sont obligatoires pour un partenaire");
            }
            u.setStatut(StatutCompte.EN_ATTENTE);   // activé ensuite par l'administrateur
            u.attacherPartenaire(new ProfilPartenaire(u, r.nomSociete().trim(), r.typePartenaire(), r.numeroFiscal()));
        } else {
            u.setStatut(StatutCompte.ACTIF);
        }
        return mapper.toUser(users.save(u));
    }

    @Transactional(readOnly = true)
    public AuthResponse connecter(LoginRequest r) {
        Utilisateur u = users.findByEmailIgnoreCase(r.email().trim())
                .filter(x -> encoder.matches(r.password(), x.getMotDePasse()))
                .orElseThrow(() -> ApiException.unauthorized("Email ou mot de passe incorrect"));
        switch (u.getStatut()) {
            case BLOQUE -> throw ApiException.forbidden("Compte bloqué");
            case EN_ATTENTE -> throw ApiException.forbidden("Compte en attente de validation par l'administrateur");
            case INACTIF -> throw ApiException.forbidden("Compte inactif");
            default -> {
            }
        }
        String token = jwt.generer(u.getId(), u.getEmail(), mapper.nomsRoles(u));
        return new AuthResponse(token, "Bearer", jwt.getExpirationSeconds(), mapper.toUser(u));
    }

    // ------------------------------------------------------------------ compte (propriétaire ou admin)
    @Transactional(readOnly = true)
    public UserResponse consulter(Long id) {
        return mapper.toUser(trouver(id));
    }

    public UserResponse modifier(Long id, UpdateProfileRequest r) {
        Utilisateur u = trouver(id);
        if (r.prenom() != null) u.setPrenom(r.prenom().trim());
        if (r.nom() != null) u.setNom(r.nom().trim());
        if (r.telephone() != null) u.setTelephone(r.telephone());
        if (r.photoProfil() != null) u.setPhotoProfil(r.photoProfil());
        if (r.nationalite() != null) u.setNationalite(r.nationalite());
        if (r.dateNaissance() != null) u.setDateNaissance(r.dateNaissance());
        if (r.adresse() != null) u.setAdresse(r.adresse());
        return mapper.toUser(users.saveAndFlush(u));
    }

    public void changerMotDePasse(Long id, ChangePasswordRequest r) {
        Utilisateur u = trouver(id);
        if (!encoder.matches(r.ancienMotDePasse(), u.getMotDePasse())) {
            throw ApiException.badRequest("Ancien mot de passe incorrect");
        }
        u.setMotDePasse(encoder.encode(r.nouveauMotDePasse()));
    }

    public void supprimer(Long id) {
        users.delete(trouver(id));
    }

    // ------------------------------------------------------------------ administration
    @Transactional(readOnly = true)
    public PageResponse<UserResponse> lister(NomRole role, StatutCompte statut, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100), Sort.by("id"));
        Page<Utilisateur> p;
        if (role != null && statut != null) p = users.findByRoles_Role_NomAndStatut(role, statut, pageable);
        else if (role != null) p = users.findByRoles_Role_Nom(role, pageable);
        else if (statut != null) p = users.findByStatut(statut, pageable);
        else p = users.findAll(pageable);
        return new PageResponse<>(p.getContent().stream().map(mapper::toUser).toList(),
                p.getNumber(), p.getSize(), p.getTotalElements(), p.getTotalPages());
    }

    public UserResponse creerParAdmin(AdminCreateUserRequest r) {
        verifierEmailLibre(r.email());
        Utilisateur u = new Utilisateur(r.prenom().trim(), r.nom().trim(), r.email().trim().toLowerCase(),
                encoder.encode(r.password()));
        u.ajouterRole(role(r.role()));
        u.setStatut(StatutCompte.ACTIF);
        return mapper.toUser(users.save(u));
    }

    public UserResponse bloquer(Long id) {
        Utilisateur u = trouver(id);
        if (u.getStatut() == StatutCompte.BLOQUE) throw ApiException.conflict("Compte déjà bloqué");
        u.setStatut(StatutCompte.BLOQUE);
        return mapper.toUser(u);
    }

    public UserResponse debloquer(Long id) {
        Utilisateur u = trouver(id);
        if (u.getStatut() != StatutCompte.BLOQUE) throw ApiException.conflict("Le compte n'est pas bloqué");
        u.setStatut(StatutCompte.ACTIF);
        return mapper.toUser(u);
    }

    public UserResponse validerPartenaire(Long id) {
        Utilisateur u = trouver(id);
        if (!u.aRole(NomRole.PARTENAIRE)) throw ApiException.badRequest("Ce compte n'est pas un partenaire");
        if (u.getStatut() != StatutCompte.EN_ATTENTE) throw ApiException.conflict("Le partenaire n'est pas en attente");
        u.setStatut(StatutCompte.ACTIF);
        return mapper.toUser(u);
    }

    public UserResponse ajouterRole(Long id, NomRole nom) {
        Utilisateur u = trouver(id);
        if (u.aRole(nom)) throw ApiException.conflict("L'utilisateur a déjà le rôle " + nom);
        u.ajouterRole(role(nom));
        return mapper.toUser(users.saveAndFlush(u));
    }

    public UserResponse retirerRole(Long id, NomRole nom) {
        Utilisateur u = trouver(id);
        if (!u.aRole(nom)) throw ApiException.notFound("L'utilisateur n'a pas le rôle " + nom);
        if (u.getRoles().size() == 1) throw ApiException.badRequest("Un utilisateur doit garder au moins un rôle");
        u.retirerRole(nom);
        return mapper.toUser(users.saveAndFlush(u));
    }

    public void creerAdministrateurSiAbsent(String email, String motDePasse) {
        if (users.existsByEmailIgnoreCase(email)) return;
        Utilisateur a = new Utilisateur("Admin", "TravelMate", email.toLowerCase(), encoder.encode(motDePasse));
        a.ajouterRole(role(NomRole.ADMINISTRATEUR));
        a.setStatut(StatutCompte.ACTIF);
        users.save(a);
    }

    // ------------------------------------------------------------------ appels entre services
    @Transactional(readOnly = true)
    public UserIdentity identite(Long id) {
        Utilisateur u = trouver(id);
        return new UserIdentity(u.getId(), u.getEmail(), u.getStatut(), mapper.nomsRoles(u));
    }

    // ------------------------------------------------------------------ utilitaires
    private Utilisateur trouver(Long id) {
        return users.findById(id).orElseThrow(() -> ApiException.notFound("Utilisateur " + id + " introuvable"));
    }

    private Role role(NomRole nom) {
        return roles.findByNom(nom).orElseThrow(() -> new IllegalStateException("Rôle manquant : " + nom));
    }

    private void verifierEmailLibre(String email) {
        if (users.existsByEmailIgnoreCase(email.trim())) throw ApiException.conflict("Email déjà utilisé");
    }
}
