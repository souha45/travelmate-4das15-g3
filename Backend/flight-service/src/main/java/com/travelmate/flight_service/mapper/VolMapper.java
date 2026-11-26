package com.travelmate.flight_service.mapper;

import com.travelmate.flight_service.dto.VolRequest;
import com.travelmate.flight_service.dto.VolResponse;
import com.travelmate.flight_service.entity.Aeroport;
import com.travelmate.flight_service.entity.Compagnie;
import com.travelmate.flight_service.entity.Vol;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.RoundingMode;

@Component
@RequiredArgsConstructor
public class VolMapper {

    private final CompagnieMapper compagnieMapper;
    private final AeroportMapper aeroportMapper;

    // Sert à la création et à la modification (les relations sont résolues par le service)
    public void appliquer(Vol vol, VolRequest r, Compagnie compagnie,
                          Aeroport depart, Aeroport arrivee) {
        vol.setNumeroVol(r.numeroVol().trim().toUpperCase());
        vol.setDateHeureDepart(r.dateHeureDepart());
        vol.setDateHeureArrivee(r.dateHeureArrivee());
        vol.setPrix(r.prix().setScale(2, RoundingMode.HALF_UP));
        vol.setNbrePlacesTotal(r.nbrePlacesTotal());
        vol.setCompagnie(compagnie);
        vol.setAeroportDepart(depart);
        vol.setAeroportArrivee(arrivee);
    }

    public VolResponse toResponse(Vol v) {
        return new VolResponse(
                v.getId(), v.getNumeroVol(), v.getDateHeureDepart(), v.getDateHeureArrivee(),
                v.getPrix(), v.getNbrePlacesTotal(), v.getNbrePlacesDispo(),
                v.getStatutVol(), v.getModeration(),
                compagnieMapper.toResponse(v.getCompagnie()),
                aeroportMapper.toResponse(v.getAeroportDepart()),
                aeroportMapper.toResponse(v.getAeroportArrivee()));
    }
}