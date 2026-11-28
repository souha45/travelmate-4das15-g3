package com.travelmate.flight_service.service;

import com.travelmate.flight_service.dto.CompagnieRequest;
import com.travelmate.flight_service.dto.CompagnieResponse;
import com.travelmate.flight_service.entity.Compagnie;
import com.travelmate.flight_service.enums.StatutCompagnie;
import com.travelmate.flight_service.exception.BusinessException;
import com.travelmate.flight_service.exception.DuplicateResourceException;
import com.travelmate.flight_service.exception.ResourceNotFoundException;
import com.travelmate.flight_service.mapper.CompagnieMapper;
import com.travelmate.flight_service.repository.CompagnieRepository;
import com.travelmate.flight_service.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompagnieService {

    private final CompagnieRepository compagnieRepository;
    private final CompagnieMapper mapper;

    /** Le partenaire connecté crée SA compagnie (une seule). partenaireId vient du token. */
    @Transactional
    public CompagnieResponse creer(CompagnieRequest request) {
        CurrentUser.exigerTypeCompagnieAerienne();
        Long partenaireId = CurrentUser.id();

        if (compagnieRepository.existsByPartenaireId(partenaireId)) {
            throw new DuplicateResourceException("Vous avez déjà une compagnie : un partenaire ne peut en avoir qu'une");
        }
        String code = request.codeCompagnie().trim().toUpperCase();
        if (compagnieRepository.existsByCodeCompagnie(code)) {
            throw new DuplicateResourceException("Une compagnie avec le code '" + code + "' existe déjà");
        }

        Compagnie compagnie = mapper.toEntity(request);
        compagnie.setPartenaireId(partenaireId);
        compagnie.setStatut(StatutCompagnie.ACTIVE);
        return mapper.toResponse(compagnieRepository.save(compagnie));
    }

    /** Public : seules les compagnies ACTIVE. L'admin voit tout. */
    @Transactional(readOnly = true)
    public List<CompagnieResponse> lireToutes(String pays) {
        boolean avecPays = pays != null && !pays.isBlank();
        List<Compagnie> compagnies = CurrentUser.isAdmin()
                ? compagnieRepository.findAll()
                : compagnieRepository.findByStatut(StatutCompagnie.ACTIVE);
        return compagnies.stream()
                .filter(c -> !avecPays || c.getPaysCompagnie().equalsIgnoreCase(pays.trim()))
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CompagnieResponse lireMaCompagnie() {
        Compagnie c = compagnieRepository.findByPartenaireId(CurrentUser.id())
                .orElseThrow(() -> new ResourceNotFoundException("Vous n'avez pas encore créé de compagnie"));
        return mapper.toResponse(c);
    }

    /** Une compagnie non active n'est visible que par son propriétaire et l'admin (sinon 404). */
    @Transactional(readOnly = true)
    public CompagnieResponse lireParId(Long id) {
        Compagnie c = trouver(id);
        if (c.getStatut() != StatutCompagnie.ACTIVE) {
            Long moi = CurrentUser.idOrNull();
            boolean proprietaire = moi != null && moi.equals(c.getPartenaireId());
            if (!CurrentUser.isAdmin() && !proprietaire) {
                throw new ResourceNotFoundException("Compagnie introuvable avec l'id " + id);
            }
        }
        return mapper.toResponse(c);
    }

    @Transactional
    public CompagnieResponse modifier(Long id, CompagnieRequest request) {
        Compagnie compagnie = trouver(id);
        CurrentUser.exigerProprietaireOuAdmin(compagnie.getPartenaireId());
        if (compagnie.getStatut() == StatutCompagnie.SUSPENDUE && !CurrentUser.isAdmin()) {
            throw new BusinessException("Compagnie suspendue par l'administrateur : modification impossible");
        }

        String code = request.codeCompagnie().trim().toUpperCase();
        compagnieRepository.findByCodeCompagnie(code)
                .filter(autre -> !autre.getId().equals(id))
                .ifPresent(autre -> {
                    throw new DuplicateResourceException("Le code '" + code + "' est déjà utilisé");
                });

        mapper.updateEntity(compagnie, request);
        return mapper.toResponse(compagnieRepository.save(compagnie));
    }

    /** DELETE : pas de suppression, la compagnie passe INACTIVE et ses vols sont masqués. */
    @Transactional
    public CompagnieResponse desactiver(Long id) {
        Compagnie compagnie = trouver(id);
        CurrentUser.exigerProprietaireOuAdmin(compagnie.getPartenaireId());
        if (compagnie.getStatut() == StatutCompagnie.INACTIVE) {
            throw new BusinessException("La compagnie est déjà inactive");
        }
        if (compagnie.getStatut() == StatutCompagnie.SUSPENDUE && !CurrentUser.isAdmin()) {
            throw new BusinessException("Compagnie suspendue par l'administrateur");
        }
        compagnie.setStatut(StatutCompagnie.INACTIVE);
        return mapper.toResponse(compagnieRepository.save(compagnie));
    }

    @Transactional
    public CompagnieResponse reactiver(Long id) {
        Compagnie compagnie = trouver(id);
        CurrentUser.exigerProprietaireOuAdmin(compagnie.getPartenaireId());
        if (compagnie.getStatut() == StatutCompagnie.ACTIVE) {
            throw new BusinessException("La compagnie est déjà active");
        }
        if (compagnie.getStatut() == StatutCompagnie.SUSPENDUE && !CurrentUser.isAdmin()) {
            throw new BusinessException("Compagnie suspendue par l'administrateur : seul un administrateur peut la réactiver");
        }
        compagnie.setStatut(StatutCompagnie.ACTIVE);
        return mapper.toResponse(compagnieRepository.save(compagnie));
    }

    @Transactional
    public CompagnieResponse suspendre(Long id) {
        Compagnie compagnie = trouver(id);
        if (!CurrentUser.isAdmin()) {
            throw new com.travelmate.flight_service.exception.ForbiddenException("Réservé à l'administrateur");
        }
        if (compagnie.getStatut() == StatutCompagnie.SUSPENDUE) {
            throw new BusinessException("La compagnie est déjà suspendue");
        }
        compagnie.setStatut(StatutCompagnie.SUSPENDUE);
        return mapper.toResponse(compagnieRepository.save(compagnie));
    }

    private Compagnie trouver(Long id) {
        return compagnieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Compagnie introuvable avec l'id " + id));
    }
}