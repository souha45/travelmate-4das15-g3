package com.travelmate.flight_service.mapper;

import com.travelmate.flight_service.dto.PassagerRequest;
import com.travelmate.flight_service.dto.PassagerResponse;
import com.travelmate.flight_service.entity.Passager;
import org.springframework.stereotype.Component;

@Component
public class PassagerMapper {

    public Passager toEntity(PassagerRequest r) {
        Passager p = new Passager();
        updateEntity(p, r);
        return p;
    }

    public void updateEntity(Passager p, PassagerRequest r) {
        p.setPrenom(r.prenom().trim());
        p.setNom(r.nom().trim());
        p.setDateNaissance(r.dateNaissance());
        p.setNumPasseport(r.numPasseport().trim().toUpperCase());
        p.setNationalite(r.nationalite().trim());
        p.setNumeroSiege(r.numeroSiege() == null ? null : r.numeroSiege().trim().toUpperCase());
    }

    public PassagerResponse toResponse(Passager p) {
        return new PassagerResponse(p.getId(), p.getPrenom(), p.getNom(), p.getDateNaissance(),
                p.getNumPasseport(), p.getNationalite(), p.getNumeroSiege());
    }
}