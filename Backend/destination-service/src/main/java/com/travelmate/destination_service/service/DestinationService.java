package com.travelmate.destination_service.service;

import com.travelmate.destination_service.dto.DestinationRequest;
import com.travelmate.destination_service.exception.ApiException;
import com.travelmate.destination_service.model.Destination;
import com.travelmate.destination_service.enums.TypeDestination;
import com.travelmate.destination_service.repository.DestinationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class DestinationService {

    private final DestinationRepository repository;
    private final MongoTemplate mongo;

    public Page<Destination> rechercher(String pays, TypeDestination type, String recherche, Pageable pageable) {
        Query query = new Query(Criteria.where("actif").is(true));
        if (pays != null && !pays.isBlank()) {
            query.addCriteria(Criteria.where("pays").regex("^" + Pattern.quote(pays.trim()) + "$", "i"));
        }
        if (type != null) {
            query.addCriteria(Criteria.where("type").is(type));
        }
        if (recherche != null && !recherche.isBlank()) {
            query.addCriteria(Criteria.where("nom").regex(Pattern.quote(recherche.trim()), "i"));
        }
        long total = mongo.count(query, Destination.class);
        List<Destination> liste = mongo.find(query.with(pageable), Destination.class);
        return new PageImpl<>(liste, pageable, total);
    }

    public List<String> pays() {
        Query query = new Query(Criteria.where("actif").is(true));
        List<String> pays = new ArrayList<>(mongo.findDistinct(query, "pays", Destination.class, String.class));
        Collections.sort(pays);
        return pays;
    }

    public Destination obtenir(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Destination introuvable."));
    }

    public Destination obtenirActive(String id) {
        Destination d = obtenir(id);
        if (!d.isActif()) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Destination introuvable.");
        }
        return d;
    }

    public Destination creer(DestinationRequest r) {
        if (repository.existsByNomIgnoreCaseAndPaysIgnoreCase(r.nom(), r.pays())) {
            throw new ApiException(HttpStatus.CONFLICT, "Cette destination existe déjà.");
        }
        Destination d = new Destination();
        appliquer(d, r);
        d.setActif(true);
        return repository.save(d);
    }

    public Destination modifier(String id, DestinationRequest r) {
        Destination d = obtenir(id);
        appliquer(d, r);
        return repository.save(d);
    }

    public Destination changerStatut(String id, boolean actif) {
        Destination d = obtenir(id);
        d.setActif(actif);
        return repository.save(d);
    }

    private void appliquer(Destination d, DestinationRequest r) {
        d.setNom(r.nom());
        d.setType(r.type());
        d.setPays(r.pays());
        d.setCodePays(r.codePays());
        d.setDescription(r.description());
        d.setClimat(r.climat());
        d.setMeilleureSaison(r.meilleureSaison());
        d.setLangue(r.langue());
        d.setDevise(r.devise());
        d.setFuseauHoraire(r.fuseauHoraire());
        d.setImages(r.images());
        d.setCoordonnees(r.coordonnees());
    }
}
