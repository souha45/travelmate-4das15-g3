package com.travelmate.hebergement.dtos;

import com.travelmate.hebergement.entities.Hebergement;
import org.springframework.stereotype.Component;

@Component
public class HebergementMapper {

    // Convertir Request -> Entité (pour sauvegarder en base)
    public Hebergement toEntity(HebergementRequest request) {
        Hebergement hebergement = new Hebergement();
        hebergement.setNom(request.getNom());
        hebergement.setType(request.getType());
        hebergement.setDescription(request.getDescription());
        hebergement.setAdresse(request.getAdresse());
        hebergement.setVille(request.getVille());
        hebergement.setPays(request.getPays());
        hebergement.setLatitude(request.getLatitude());
        hebergement.setLongitude(request.getLongitude());
        hebergement.setEtoiles(request.getEtoiles());
        hebergement.setPartenaireId(request.getPartenaireId());
        return hebergement;
    }

    // Convertir Entité -> Response (pour renvoyer au Frontend)
    public HebergementResponse toResponse(Hebergement entity) {
        HebergementResponse response = new HebergementResponse();
        response.setId(entity.getId());
        response.setNom(entity.getNom());
        response.setType(entity.getType());
        response.setDescription(entity.getDescription());
        response.setAdresse(entity.getAdresse());
        response.setVille(entity.getVille());
        response.setPays(entity.getPays());
        response.setLatitude(entity.getLatitude());
        response.setLongitude(entity.getLongitude());
        response.setEtoiles(entity.getEtoiles());
        response.setPartenaireId(entity.getPartenaireId());
        return response;
    }
}