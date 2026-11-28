package com.travelmate.hebergement.dtos;

import com.travelmate.hebergement.entities.Chambre;
import org.springframework.stereotype.Component;

@Component
public class ChambreMapper {

    public Chambre toEntity(ChambreRequest request) {
        Chambre chambre = new Chambre();
        chambre.setType(request.getType());
        chambre.setCapacite(request.getCapacite());
        chambre.setPrixParNuit(request.getPrixParNuit());
        chambre.setNombreTotal(request.getNombreTotal());
        chambre.setDescription(request.getDescription());
        return chambre;
    }

    public ChambreResponse toResponse(Chambre entity) {
        ChambreResponse response = new ChambreResponse();
        response.setId(entity.getId());
        response.setType(entity.getType());
        response.setCapacite(entity.getCapacite());
        response.setPrixParNuit(entity.getPrixParNuit());
        response.setNombreTotal(entity.getNombreTotal());
        response.setDescription(entity.getDescription());
        if (entity.getHebergement() != null) {
            response.setHebergementId(entity.getHebergement().getId());
        }
        return response;
    }
}
