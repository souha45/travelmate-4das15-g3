package com.travelmate.flight_service.mapper;


import com.travelmate.flight_service.dto.CompagnieRequest;
import com.travelmate.flight_service.dto.CompagnieResponse;
import com.travelmate.flight_service.entity.Compagnie;
import org.springframework.stereotype.Component;

@Component
public class CompagnieMapper {

    public Compagnie toEntity(CompagnieRequest request) {
        Compagnie c = new Compagnie();
        updateEntity(c, request);
        return c;
    }

    // Sert à la création et à la modification
    public void updateEntity(Compagnie c, CompagnieRequest request) {
        c.setNomCompagnie(request.nomCompagnie().trim());
        c.setCodeCompagnie(request.codeCompagnie().trim().toUpperCase());
        c.setPaysCompagnie(request.paysCompagnie().trim());
    }

    public CompagnieResponse toResponse(Compagnie c) {
        return new CompagnieResponse(c.getId(), c.getNomCompagnie(), c.getCodeCompagnie(),
                c.getPaysCompagnie(), c.getPartenaireId(), c.getStatut());
    }


}