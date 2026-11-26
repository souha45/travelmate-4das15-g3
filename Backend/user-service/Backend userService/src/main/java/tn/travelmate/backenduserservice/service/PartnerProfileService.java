package tn.travelmate.backenduserservice.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.travelmate.backenduserservice.dto.Dtos.PartenaireResponse;
import tn.travelmate.backenduserservice.dto.Dtos.PartnerProfileRequest;
import tn.travelmate.backenduserservice.ennum.NomRole;
import tn.travelmate.backenduserservice.entity.ProfilPartenaire;
import tn.travelmate.backenduserservice.entity.Utilisateur;
import tn.travelmate.backenduserservice.exception.ApiException;
import tn.travelmate.backenduserservice.repository.ProfilPartenaireRepository;
import tn.travelmate.backenduserservice.repository.UtilisateurRepository;

/** CRUD du profil partenaire (0..1 par compte, réservé aux comptes de rôle PARTENAIRE). */
@Service
@Transactional
public class PartnerProfileService {
    private final UtilisateurRepository users;
    private final ProfilPartenaireRepository profils;
    private final UserMapper mapper;

    public PartnerProfileService(UtilisateurRepository users, ProfilPartenaireRepository profils, UserMapper mapper) {
        this.users = users;
        this.profils = profils;
        this.mapper = mapper;
    }

    public PartenaireResponse creer(Long userId, PartnerProfileRequest r) {
        Utilisateur u = utilisateur(userId);
        if (!u.aRole(NomRole.PARTENAIRE)) throw ApiException.badRequest("Ce compte n'a pas le rôle PARTENAIRE");
        if (u.getProfilPartenaire() != null) throw ApiException.conflict("Ce partenaire a déjà un profil");
        ProfilPartenaire p = new ProfilPartenaire(u, r.nomSociete().trim(), r.typePartenaire(), r.numeroFiscal());
        u.attacherPartenaire(p);
        return mapper.toPartner(profils.save(p));
    }

    @Transactional(readOnly = true)
    public PartenaireResponse consulter(Long userId) {
        return mapper.toPartner(profil(utilisateur(userId)));
    }

    public PartenaireResponse modifier(Long userId, PartnerProfileRequest r) {
        ProfilPartenaire p = profil(utilisateur(userId));
        p.setNomSociete(r.nomSociete().trim());
        p.setTypePartenaire(r.typePartenaire());
        p.setNumeroFiscal(r.numeroFiscal());
        return mapper.toPartner(profils.save(p));
    }

    public void supprimer(Long userId) {
        Utilisateur u = utilisateur(userId);
        ProfilPartenaire p = profil(u);
        u.detacherPartenaire();
        profils.delete(p);
    }

    private Utilisateur utilisateur(Long id) {
        return users.findById(id).orElseThrow(() -> ApiException.notFound("Utilisateur " + id + " introuvable"));
    }

    private ProfilPartenaire profil(Utilisateur u) {
        ProfilPartenaire p = u.getProfilPartenaire();
        if (p == null) throw ApiException.notFound("Aucun profil partenaire pour l'utilisateur " + u.getId());
        return p;
    }
}
