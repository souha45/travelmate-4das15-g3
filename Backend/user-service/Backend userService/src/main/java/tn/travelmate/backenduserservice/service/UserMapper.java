package tn.travelmate.backenduserservice.service;

import org.springframework.stereotype.Component;
import tn.travelmate.backenduserservice.dto.Dtos.PartenaireResponse;
import tn.travelmate.backenduserservice.dto.Dtos.PreferenceResponse;
import tn.travelmate.backenduserservice.dto.Dtos.UserResponse;
import tn.travelmate.backenduserservice.entity.PreferenceUtilisateur;
import tn.travelmate.backenduserservice.entity.ProfilPartenaire;
import tn.travelmate.backenduserservice.entity.Utilisateur;

import java.util.List;

/** Conversion des entités en objets de réponse (le mot de passe n'est jamais renvoyé). */
@Component
public class UserMapper {

    public List<String> nomsRoles(Utilisateur u) {
        return u.getRoles().stream().map(ur -> ur.getRole().getNom().name()).toList();
    }

    public UserResponse toUser(Utilisateur u) {
        ProfilPartenaire pp = u.getProfilPartenaire();
        return new UserResponse(u.getId(), u.getPrenom(), u.getNom(), u.getEmail(), u.getTelephone(),
                u.getPhotoProfil(), u.getNationalite(), u.getDateNaissance(), u.getAdresse(), u.getStatut(),
                nomsRoles(u), pp == null ? null : toPartner(pp), u.getDateCreation(), u.getDateModification());
    }

    public PartenaireResponse toPartner(ProfilPartenaire p) {
        return new PartenaireResponse(p.getId(), p.getNomSociete(), p.getTypePartenaire(), p.getNumeroFiscal());
    }

    public PreferenceResponse toPreference(PreferenceUtilisateur p) {
        return new PreferenceResponse(p.getId(), p.getTypeVoyagePrefere(), p.getDestinationPreferee(),
                p.getLanguePreferee(), p.getDevisePreferee(), p.getBudgetMin(), p.getBudgetMax(),
                p.getNombreVoyageursPrefere());
    }
}
