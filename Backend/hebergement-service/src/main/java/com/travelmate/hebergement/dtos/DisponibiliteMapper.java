package com.travelmate.hebergement.dtos;

import com.travelmate.hebergement.entities.Disponibilite;
import org.springframework.stereotype.Component;

@Component
public class DisponibiliteMapper {

    public Disponibilite toEntity(DisponibiliteRequest request) {
        Disponibilite disponibilite = new Disponibilite();
        disponibilite.setDate(request.getDate());
        disponibilite.setQuantiteRestante(request.getQuantiteRestante());
        disponibilite.setPrix(request.getPrix());
        return disponibilite;
    }

    public DisponibiliteResponse toResponse(Disponibilite entity) {
        DisponibiliteResponse response = new DisponibiliteResponse();
        response.setId(entity.getId());
        response.setDate(entity.getDate());
        response.setQuantiteRestante(entity.getQuantiteRestante());
        response.setPrix(entity.getPrix());
        if (entity.getChambre() != null) {
            response.setChambreId(entity.getChambre().getId());
        }
        return response;
    }
}
