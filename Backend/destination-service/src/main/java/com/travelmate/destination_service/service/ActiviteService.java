package com.travelmate.destination_service.service;

import com.travelmate.destination_service.dto.ActiviteRequest;
import com.travelmate.destination_service.exception.ApiException;
import com.travelmate.destination_service.model.Activite;
import com.travelmate.destination_service.enums.Categorie;
import com.travelmate.destination_service.repository.ActiviteRepository;
import com.travelmate.destination_service.repository.AvisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ActiviteService {

    private final ActiviteRepository repository;
    private final AvisRepository avisRepository;
    private final DestinationService destinationService;
    private final MongoTemplate mongo;

    public Page<Activite> lister(String destinationId, Categorie categorie, Double prixMax, Pageable pageable) {
        destinationService.obtenirActive(destinationId);
        Query query = new Query(Criteria.where("destinationId").is(destinationId));
        if (categorie != null) {
            query.addCriteria(Criteria.where("categorie").is(categorie));
        }
        if (prixMax != null) {
            query.addCriteria(Criteria.where("prix").lte(prixMax));
        }
        long total = mongo.count(query, Activite.class);
        List<Activite> liste = mongo.find(query.with(pageable), Activite.class);
        return new PageImpl<>(liste, pageable, total);
    }

    public Activite obtenir(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Activité introuvable."));
    }

    public Activite creer(String destinationId, ActiviteRequest r) {
        destinationService.obtenir(destinationId);
        Activite a = new Activite();
        a.setDestinationId(destinationId);
        appliquer(a, r);
        return repository.save(a);
    }

    public Activite modifier(String id, ActiviteRequest r) {
        Activite a = obtenir(id);
        appliquer(a, r);
        return repository.save(a);
    }

    public void supprimer(String id) {
        Activite a = obtenir(id);
        avisRepository.deleteByActiviteId(id);
        repository.delete(a);
    }

    private void appliquer(Activite a, ActiviteRequest r) {
        a.setNom(r.nom());
        a.setDescription(r.description());
        a.setCategorie(r.categorie());
        a.setPrix(r.prix());
        a.setDevise(r.devise());
        a.setLieu(r.lieu());
        a.setHoraires(r.horaires());
        a.setImages(r.images());
    }
}