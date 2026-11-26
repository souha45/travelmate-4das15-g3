package tn.travelmate.backenduserservice.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.travelmate.backenduserservice.dto.Dtos.PreferenceRequest;
import tn.travelmate.backenduserservice.dto.Dtos.PreferenceResponse;
import tn.travelmate.backenduserservice.entity.PreferenceUtilisateur;
import tn.travelmate.backenduserservice.entity.Utilisateur;
import tn.travelmate.backenduserservice.exception.ApiException;
import tn.travelmate.backenduserservice.repository.PreferenceUtilisateurRepository;
import tn.travelmate.backenduserservice.repository.UtilisateurRepository;

/** CRUD des préférences de voyage (0..1 par utilisateur). */
@Service
@Transactional
public class PreferenceService {
    private final UtilisateurRepository users;
    private final PreferenceUtilisateurRepository preferences;
    private final UserMapper mapper;

    public PreferenceService(UtilisateurRepository users, PreferenceUtilisateurRepository preferences, UserMapper mapper) {
        this.users = users;
        this.preferences = preferences;
        this.mapper = mapper;
    }

    public PreferenceResponse creer(Long userId, PreferenceRequest r) {
        Utilisateur u = utilisateur(userId);
        if (u.getPreference() != null) throw ApiException.conflict("Des préférences existent déjà pour cet utilisateur");
        PreferenceUtilisateur p = new PreferenceUtilisateur(u);
        appliquer(p, r);
        u.attacherPreference(p);
        return mapper.toPreference(preferences.save(p));
    }

    @Transactional(readOnly = true)
    public PreferenceResponse consulter(Long userId) {
        return mapper.toPreference(preference(utilisateur(userId)));
    }

    public PreferenceResponse modifier(Long userId, PreferenceRequest r) {
        PreferenceUtilisateur p = preference(utilisateur(userId));
        appliquer(p, r);
        return mapper.toPreference(preferences.save(p));
    }

    public void supprimer(Long userId) {
        Utilisateur u = utilisateur(userId);
        PreferenceUtilisateur p = preference(u);
        u.detacherPreference();
        preferences.delete(p);
    }

    private Utilisateur utilisateur(Long id) {
        return users.findById(id).orElseThrow(() -> ApiException.notFound("Utilisateur " + id + " introuvable"));
    }

    private PreferenceUtilisateur preference(Utilisateur u) {
        PreferenceUtilisateur p = u.getPreference();
        if (p == null) throw ApiException.notFound("Aucune préférence définie pour l'utilisateur " + u.getId());
        return p;
    }

    private void appliquer(PreferenceUtilisateur p, PreferenceRequest r) {
        if (r.budgetMin() != null && r.budgetMax() != null && r.budgetMin() > r.budgetMax()) {
            throw ApiException.badRequest("budgetMin doit être inférieur ou égal à budgetMax");
        }
        p.setTypeVoyagePrefere(r.typeVoyagePrefere());
        p.setDestinationPreferee(r.destinationPreferee());
        p.setLanguePreferee(r.languePreferee());
        p.setDevisePreferee(r.devisePreferee());
        p.setBudgetMin(r.budgetMin());
        p.setBudgetMax(r.budgetMax());
        p.setNombreVoyageursPrefere(r.nombreVoyageursPrefere());
    }
}
