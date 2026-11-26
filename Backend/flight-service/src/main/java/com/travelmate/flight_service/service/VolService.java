package com.travelmate.flight_service.service;

import com.travelmate.flight_service.dto.RetardRequest;
import com.travelmate.flight_service.dto.VolRequest;
import com.travelmate.flight_service.dto.VolResponse;
import com.travelmate.flight_service.entity.Aeroport;
import com.travelmate.flight_service.entity.Compagnie;
import com.travelmate.flight_service.entity.Vol;
import com.travelmate.flight_service.enums.StatutModeration;
import com.travelmate.flight_service.enums.StatutVol;
import com.travelmate.flight_service.exception.BusinessException;
import com.travelmate.flight_service.exception.DuplicateResourceException;
import com.travelmate.flight_service.exception.InvalidRequestException;
import com.travelmate.flight_service.exception.ResourceNotFoundException;
import com.travelmate.flight_service.mapper.VolMapper;
import com.travelmate.flight_service.repository.AeroportRepository;
import com.travelmate.flight_service.repository.CompagnieRepository;
import com.travelmate.flight_service.repository.ReservationRepository;
import com.travelmate.flight_service.repository.VolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VolService {

    private final VolRepository volRepository;
    private final CompagnieRepository compagnieRepository;
    private final AeroportRepository aeroportRepository;
    private final ReservationRepository reservationRepository;
    private final VolMapper mapper;

    // ---------- CRUD ----------

    @Transactional
    public VolResponse creer(VolRequest request) {
        String numero = request.numeroVol().trim().toUpperCase();
        if (volRepository.existsByNumeroVol(numero)) {
            throw new DuplicateResourceException("Un vol avec le numéro '" + numero + "' existe déjà");
        }
        verifierAeroportsDifferents(request);
        verifierDates(request.dateHeureDepart(), request.dateHeureArrivee());
        if (request.dateHeureDepart().isBefore(LocalDateTime.now())) {
            throw new InvalidRequestException("La date de départ doit être dans le futur");
        }

        Compagnie compagnie = trouverCompagnie(request.compagnieId());
        Aeroport depart = trouverAeroport(request.aeroportDepartId());
        Aeroport arrivee = trouverAeroport(request.aeroportArriveeId());

        Vol vol = new Vol();
        mapper.appliquer(vol, request, compagnie, depart, arrivee);
        vol.setNbrePlacesDispo(request.nbrePlacesTotal());
        vol.setStatutVol(StatutVol.PROGRAMME);
        vol.setModeration(StatutModeration.EN_REVISION);
        return mapper.toResponse(volRepository.save(vol));
    }

    @Transactional(readOnly = true)
    public List<VolResponse> lireTous(Long compagnieId, StatutVol statut, StatutModeration moderation) {
        List<Vol> vols = (compagnieId != null)
                ? volRepository.findByCompagnieId(compagnieId)
                : volRepository.findAll();
        return vols.stream()
                .filter(v -> statut == null || v.getStatutVol() == statut)
                .filter(v -> moderation == null || v.getModeration() == moderation)
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public VolResponse lireParId(Long id) {
        return mapper.toResponse(trouver(id));
    }

    @Transactional(readOnly = true)
    public VolResponse lireParNumero(String numero) {
        String numeroNormalise = numero.trim().toUpperCase();
        Vol vol = volRepository.findByNumeroVol(numeroNormalise)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Vol introuvable avec le numéro " + numeroNormalise));
        return mapper.toResponse(vol);
    }

    @Transactional
    public VolResponse modifier(Long id, VolRequest request) {
        Vol vol = trouver(id);
        if (vol.getStatutVol() == StatutVol.ANNULE || vol.getStatutVol() == StatutVol.TERMINE) {
            throw new BusinessException("Un vol annulé ou terminé ne peut plus être modifié");
        }

        // On ne peut pas descendre sous le nombre de places déjà réservées
        int reservees = vol.getNbrePlacesTotal() - vol.getNbrePlacesDispo();
        if (request.nbrePlacesTotal() < reservees) {
            throw new BusinessException("Impossible de réduire à " + request.nbrePlacesTotal()
                    + " places : " + reservees + " sont déjà réservées");
        }

        String numero = request.numeroVol().trim().toUpperCase();
        volRepository.findByNumeroVol(numero)
                .filter(autre -> !autre.getId().equals(id))
                .ifPresent(autre -> {
                    throw new DuplicateResourceException("Le numéro '" + numero + "' est déjà utilisé");
                });

        verifierAeroportsDifferents(request);
        verifierDates(request.dateHeureDepart(), request.dateHeureArrivee());
        boolean departModifie = !request.dateHeureDepart().equals(vol.getDateHeureDepart());
        if (departModifie && request.dateHeureDepart().isBefore(LocalDateTime.now())) {
            throw new InvalidRequestException("La nouvelle date de départ doit être dans le futur");
        }

        Compagnie compagnie = trouverCompagnie(request.compagnieId());
        Aeroport depart = trouverAeroport(request.aeroportDepartId());
        Aeroport arrivee = trouverAeroport(request.aeroportArriveeId());

        mapper.appliquer(vol, request, compagnie, depart, arrivee);
        vol.setNbrePlacesDispo(request.nbrePlacesTotal() - reservees);
        return mapper.toResponse(volRepository.save(vol));
    }

    @Transactional
    public void supprimer(Long id) {
        Vol vol = trouver(id);
        if (reservationRepository.existsByVolId(id)) {
            throw new BusinessException("Impossible de supprimer le vol : des réservations lui sont associées. "
                    + "Annulez-le plutôt.");
        }
        volRepository.delete(vol);
    }

    // ---------- Recherche publique ----------

    @Transactional(readOnly = true)
    public List<VolResponse> rechercher(String depart, String arrivee, LocalDate date, int passagers) {
        String codeDepart = depart.trim().toUpperCase();
        String codeArrivee = arrivee.trim().toUpperCase();
        if (codeDepart.equals(codeArrivee)) {
            throw new InvalidRequestException("L'aéroport de départ et d'arrivée doivent être différents");
        }
        if (passagers < 1) {
            throw new InvalidRequestException("Le nombre de passagers doit être au moins 1");
        }
        return volRepository.rechercherVols(codeDepart, codeArrivee,
                        date.atStartOfDay(), date.atTime(23, 59, 59), passagers)
                .stream().map(mapper::toResponse).toList();
    }

    // ---------- Modération ----------

    @Transactional
    public VolResponse approuver(Long id) {
        Vol vol = trouver(id);
        if (vol.getModeration() == StatutModeration.APPROUVE) {
            throw new BusinessException("Le vol est déjà approuvé");
        }
        vol.setModeration(StatutModeration.APPROUVE);
        return mapper.toResponse(volRepository.save(vol));
    }

    @Transactional
    public VolResponse suspendre(Long id) {
        Vol vol = trouver(id);
        if (vol.getModeration() == StatutModeration.SUSPENDU) {
            throw new BusinessException("Le vol est déjà suspendu");
        }
        vol.setModeration(StatutModeration.SUSPENDU);
        return mapper.toResponse(volRepository.save(vol));
    }

    // ---------- Statut du vol ----------

    @Transactional
    public VolResponse retarder(Long id, RetardRequest request) {
        Vol vol = trouver(id);
        if (vol.getStatutVol() != StatutVol.PROGRAMME && vol.getStatutVol() != StatutVol.RETARD) {
            throw new BusinessException("Seul un vol programmé ou déjà retardé peut être retardé");
        }
        verifierDates(request.nouvelleDateHeureDepart(), request.nouvelleDateHeureArrivee());
        if (!request.nouvelleDateHeureDepart().isAfter(vol.getDateHeureDepart())) {
            throw new InvalidRequestException(
                    "La nouvelle date de départ doit être postérieure à la date actuelle du vol");
        }
        vol.setDateHeureDepart(request.nouvelleDateHeureDepart());
        vol.setDateHeureArrivee(request.nouvelleDateHeureArrivee());
        vol.setStatutVol(StatutVol.RETARD);
        return mapper.toResponse(volRepository.save(vol));
    }

    @Transactional
    public VolResponse annuler(Long id) {
        Vol vol = trouver(id);
        if (vol.getStatutVol() == StatutVol.ANNULE) {
            throw new BusinessException("Le vol est déjà annulé");
        }
        if (vol.getStatutVol() == StatutVol.TERMINE) {
            throw new BusinessException("Un vol terminé ne peut pas être annulé");
        }
        vol.annuler();   // méthode de l'entité : statutVol = ANNULE
        // TODO (branche Réservation) : annuler les réservations liées et prévenir notification-service
        return mapper.toResponse(volRepository.save(vol));
    }

    // ---------- Méthodes privées ----------

    private Vol trouver(Long id) {
        return volRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vol introuvable avec l'id " + id));
    }

    private Compagnie trouverCompagnie(Long id) {
        return compagnieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Compagnie introuvable avec l'id " + id));
    }

    private Aeroport trouverAeroport(Long id) {
        return aeroportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Aéroport introuvable avec l'id " + id));
    }

    private void verifierAeroportsDifferents(VolRequest r) {
        if (r.aeroportDepartId().equals(r.aeroportArriveeId())) {
            throw new InvalidRequestException("L'aéroport de départ et d'arrivée doivent être différents");
        }
    }

    private void verifierDates(LocalDateTime depart, LocalDateTime arrivee) {
        if (!arrivee.isAfter(depart)) {
            throw new InvalidRequestException("La date d'arrivée doit être postérieure à la date de départ");
        }
    }
}