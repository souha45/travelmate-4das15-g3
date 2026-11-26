package com.travelmate.flight_service.service;

import com.travelmate.flight_service.dto.CompagnieRequest;
import com.travelmate.flight_service.dto.CompagnieResponse;
import com.travelmate.flight_service.entity.Compagnie;
import com.travelmate.flight_service.exception.BusinessException;
import com.travelmate.flight_service.exception.DuplicateResourceException;
import com.travelmate.flight_service.exception.ResourceNotFoundException;
import com.travelmate.flight_service.mapper.CompagnieMapper;
import com.travelmate.flight_service.repository.CompagnieRepository;
import com.travelmate.flight_service.repository.VolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompagnieService {

    private final CompagnieRepository compagnieRepository;
    private final VolRepository volRepository;
    private final CompagnieMapper mapper;

    @Transactional
    public CompagnieResponse creer(CompagnieRequest request) {
        String code = request.codeCompagnie().trim().toUpperCase();
        if (compagnieRepository.existsByCodeCompagnie(code)) {
            throw new DuplicateResourceException("Une compagnie avec le code '" + code + "' existe déjà");
        }
        Compagnie saved = compagnieRepository.save(mapper.toEntity(request));
        return mapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<CompagnieResponse> lireToutes(String pays) {
        List<Compagnie> compagnies = (pays == null || pays.isBlank())
                ? compagnieRepository.findAll()
                : compagnieRepository.findByPaysCompagnieIgnoreCase(pays.trim());
        return compagnies.stream().map(mapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public CompagnieResponse lireParId(Long id) {
        return mapper.toResponse(trouver(id));
    }

    @Transactional
    public CompagnieResponse modifier(Long id, CompagnieRequest request) {
        Compagnie compagnie = trouver(id);
        String code = request.codeCompagnie().trim().toUpperCase();

        // Le nouveau code ne doit pas appartenir à UNE AUTRE compagnie
        compagnieRepository.findByCodeCompagnie(code)
                .filter(autre -> !autre.getId().equals(id))
                .ifPresent(autre -> {
                    throw new DuplicateResourceException("Le code '" + code + "' est déjà utilisé");
                });

        mapper.updateEntity(compagnie, request);
        return mapper.toResponse(compagnieRepository.save(compagnie));
    }

    @Transactional
    public void supprimer(Long id) {
        Compagnie compagnie = trouver(id);
        if (volRepository.existsByCompagnieId(id)) {
            throw new BusinessException("Impossible de supprimer la compagnie : des vols lui sont associés");
        }
        compagnieRepository.delete(compagnie);
    }

    private Compagnie trouver(Long id) {
        return compagnieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Compagnie introuvable avec l'id " + id));
    }
}