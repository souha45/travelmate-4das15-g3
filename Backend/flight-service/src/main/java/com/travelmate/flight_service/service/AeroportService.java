package com.travelmate.flight_service.service;

import com.travelmate.flight_service.dto.AeroportRequest;
import com.travelmate.flight_service.dto.AeroportResponse;
import com.travelmate.flight_service.entity.Aeroport;
import com.travelmate.flight_service.exception.BusinessException;
import com.travelmate.flight_service.exception.DuplicateResourceException;
import com.travelmate.flight_service.exception.ResourceNotFoundException;
import com.travelmate.flight_service.mapper.AeroportMapper;
import com.travelmate.flight_service.repository.AeroportRepository;
import com.travelmate.flight_service.repository.VolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AeroportService {

    private final AeroportRepository aeroportRepository;
    private final VolRepository volRepository;
    private final AeroportMapper mapper;

    @Transactional
    public AeroportResponse creer(AeroportRequest request) {
        String code = request.codeAeroport().trim().toUpperCase();
        if (aeroportRepository.existsByCodeAeroport(code)) {
            throw new DuplicateResourceException("Un aéroport avec le code '" + code + "' existe déjà");
        }
        Aeroport saved = aeroportRepository.save(mapper.toEntity(request));
        return mapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<AeroportResponse> lireTous(String ville, String pays) {
        boolean avecVille = ville != null && !ville.isBlank();
        boolean avecPays = pays != null && !pays.isBlank();

        List<Aeroport> aeroports = aeroportRepository.findAll();
        return aeroports.stream()
                .filter(a -> !avecVille || a.getVille().equalsIgnoreCase(ville.trim()))
                .filter(a -> !avecPays || a.getPaysAeroport().equalsIgnoreCase(pays.trim()))
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public AeroportResponse lireParId(Long id) {
        return mapper.toResponse(trouver(id));
    }

    @Transactional(readOnly = true)
    public AeroportResponse lireParCode(String code) {
        String codeNormalise = code.trim().toUpperCase();
        Aeroport aeroport = aeroportRepository.findByCodeAeroport(codeNormalise)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Aéroport introuvable avec le code " + codeNormalise));
        return mapper.toResponse(aeroport);
    }

    @Transactional(readOnly = true)
    public List<AeroportResponse> rechercher(String q) {
        if (q == null || q.isBlank()) {
            throw new BusinessException("Le paramètre de recherche 'q' est obligatoire");
        }
        return aeroportRepository.rechercherParMotCle(q.trim()).stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional
    public AeroportResponse modifier(Long id, AeroportRequest request) {
        Aeroport aeroport = trouver(id);
        String code = request.codeAeroport().trim().toUpperCase();

        aeroportRepository.findByCodeAeroport(code)
                .filter(autre -> !autre.getId().equals(id))
                .ifPresent(autre -> {
                    throw new DuplicateResourceException("Le code '" + code + "' est déjà utilisé");
                });

        mapper.updateEntity(aeroport, request);
        return mapper.toResponse(aeroportRepository.save(aeroport));
    }

    @Transactional
    public void supprimer(Long id) {
        Aeroport aeroport = trouver(id);
        if (volRepository.existsByAeroportDepartIdOrAeroportArriveeId(id, id)) {
            throw new BusinessException("Impossible de supprimer l'aéroport : des vols y sont associés");
        }
        aeroportRepository.delete(aeroport);
    }

    private Aeroport trouver(Long id) {
        return aeroportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Aéroport introuvable avec l'id " + id));
    }
}