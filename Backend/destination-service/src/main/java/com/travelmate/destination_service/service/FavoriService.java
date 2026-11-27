package com.travelmate.destination_service.service;

import com.travelmate.destination_service.exception.ApiException;
import com.travelmate.destination_service.model.Destination;
import com.travelmate.destination_service.model.Favori;
import com.travelmate.destination_service.repository.DestinationRepository;
import com.travelmate.destination_service.repository.FavoriRepository;
import com.travelmate.destination_service.security.Utilisateur;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriService {

    private final FavoriRepository favoriRepository;
    private final DestinationRepository destinationRepository;
    private final DestinationService destinationService;

    /** Renvoie les destinations (actives) mises en favori par l'utilisateur. */
    public List<Destination> lister(Utilisateur utilisateur) {
        List<String> ids = favoriRepository.findByUtilisateurId(utilisateur.id()).stream()
                .map(Favori::getDestinationId).toList();
        return destinationRepository.findAllById(ids).stream()
                .filter(Destination::isActif).toList();
    }

    public Favori ajouter(Utilisateur utilisateur, String destinationId) {
        destinationService.obtenirActive(destinationId);
        if (favoriRepository.existsByUtilisateurIdAndDestinationId(utilisateur.id(), destinationId)) {
            throw new ApiException(HttpStatus.CONFLICT, "Cette destination est déjà dans vos favoris.");
        }
        return favoriRepository.save(Favori.builder()
                .utilisateurId(utilisateur.id())
                .destinationId(destinationId)
                .build());
    }

    public void retirer(Utilisateur utilisateur, String destinationId) {
        Favori favori = favoriRepository
                .findByUtilisateurIdAndDestinationId(utilisateur.id(), destinationId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Favori introuvable."));
        favoriRepository.delete(favori);
    }
}
