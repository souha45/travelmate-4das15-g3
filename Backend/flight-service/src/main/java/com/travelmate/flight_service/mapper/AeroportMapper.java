package com.travelmate.flight_service.mapper;

import com.travelmate.flight_service.dto.AeroportRequest;
import com.travelmate.flight_service.dto.AeroportResponse;
import com.travelmate.flight_service.entity.Aeroport;
import org.springframework.stereotype.Component;

@Component
public class AeroportMapper {

    public Aeroport toEntity(AeroportRequest request) {
        Aeroport a = new Aeroport();
        updateEntity(a, request);
        return a;
    }

    public void updateEntity(Aeroport a, AeroportRequest request) {
        a.setCodeAeroport(request.codeAeroport().trim().toUpperCase());
        a.setNomAeroport(request.nomAeroport().trim());
        a.setVille(request.ville().trim());
        a.setPaysAeroport(request.paysAeroport().trim());
    }

    public AeroportResponse toResponse(Aeroport a) {
        return new AeroportResponse(a.getId(), a.getCodeAeroport(),
                a.getNomAeroport(), a.getVille(), a.getPaysAeroport());
    }
}