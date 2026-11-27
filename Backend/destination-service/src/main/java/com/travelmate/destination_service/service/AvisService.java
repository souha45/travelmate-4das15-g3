package com.travelmate.destination_service.service;

import com.travelmate.destination_service.dto.AvisRequest;
import com.travelmate.destination_service.exception.ApiException;
import com.travelmate.destination_service.model.Activite;
import com.travelmate.destination_service.model.Avis;
import com.travelmate.destination_service.repository.ActiviteRepository;
import com.travelmate.destination_service.repository.AvisRepository;
import com.travelmate.destination_service.security.Utilisateur;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AvisService {

    private final AvisRepository avisRepository;
    private final ActiviteRepository activiteRepository;

    public Page<Avis> lister(String activiteId, Pageable pageable) {
        trouverActivite(activiteId);
        return avisRepository.findByActiviteId(activiteId, pageable);
    }

    public Avis ajouter(String activiteId, Utilisateur utilisateur, AvisRequest r) {
        trouverActivite(activiteId);
        if (avisRepository.existsByUtilisateurIdAndActiviteId(utilisateur.id(), activiteId)) {
            throw new ApiException(HttpStatus.CONFLICT, "Vous avez déjà noté cette activité.");
        }
        Avis avis = Avis.builder()
                .activiteId(activiteId)
                .utilisateurId(utilisateur.id())
                .nomUtilisateur("Voyageur")
                .note(r.note())
                .commentaire(r.commentaire())
                .build();
        Avis enregistre = avisRepository.save(avis);
        recalculer(activiteId);
        return enregistre;
    }

    public Avis modifier(String avisId, Utilisateur utilisateur, AvisRequest r) {
        Avis avis = trouverAvis(avisId);
        if (!avis.getUtilisateurId().equals(utilisateur.id())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Vous ne pouvez modifier que votre propre avis.");
        }
        avis.setNote(r.note());
        avis.setCommentaire(r.commentaire());
        Avis enregistre = avisRepository.save(avis);
        recalculer(avis.getActiviteId());
        return enregistre;
    }

    public void supprimer(String avisId, Utilisateur utilisateur) {
        Avis avis = trouverAvis(avisId);
        if (!avis.getUtilisateurId().equals(utilisateur.id()) && !utilisateur.estAdmin()) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Vous ne pouvez pas supprimer cet avis.");
        }
        avisRepository.delete(avis);
        recalculer(avis.getActiviteId());
    }

    /** Recalcule la note moyenne et le nombre d'avis de l'activité. */
    private void recalculer(String activiteId) {
        Activite activite = trouverActivite(activiteId);
        List<Avis> avis = avisRepository.findAllByActiviteId(activiteId);
        double moyenne = avis.stream().mapToInt(Avis::getNote).average().orElse(0);
        activite.setNote(Math.round(moyenne * 10.0) / 10.0);
        activite.setNombreAvis(avis.size());
        activiteRepository.save(activite);
    }

    private Activite trouverActivite(String id) {
        return activiteRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Activité introuvable."));
    }

    private Avis trouverAvis(String id) {
        return avisRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Avis introuvable."));
    }
}
