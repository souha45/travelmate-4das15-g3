package com.travelmate.flight_service.service;

import com.travelmate.flight_service.dto.*;
import com.travelmate.flight_service.entity.Passager;
import com.travelmate.flight_service.entity.Reservation;
import com.travelmate.flight_service.entity.Vol;
import com.travelmate.flight_service.enums.StatutCompagnie;
import com.travelmate.flight_service.enums.StatutModeration;
import com.travelmate.flight_service.enums.StatutReservation;
import com.travelmate.flight_service.enums.StatutVol;
import com.travelmate.flight_service.exception.BusinessException;
import com.travelmate.flight_service.exception.ForbiddenException;
import com.travelmate.flight_service.exception.InvalidRequestException;
import com.travelmate.flight_service.exception.ResourceNotFoundException;
import com.travelmate.flight_service.mapper.PassagerMapper;
import com.travelmate.flight_service.mapper.ReservationMapper;
import com.travelmate.flight_service.repository.PassagerRepository;
import com.travelmate.flight_service.repository.ReservationRepository;
import com.travelmate.flight_service.repository.VolRepository;
import com.travelmate.flight_service.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private static final List<StatutReservation> STATUTS_ACTIFS =
            List.of(StatutReservation.EN_ATTENTE, StatutReservation.CONFIRMEE);

    private final ReservationRepository reservationRepository;
    private final PassagerRepository passagerRepository;
    private final VolRepository volRepository;
    private final ReservationMapper mapper;
    private final PassagerMapper passagerMapper;

    @Value("${flight.reservation.duree-expiration-minutes:30}")
    private long dureeExpirationMinutes;



    // ---------- Création ----------

    @Transactional
    public ReservationResponse creer(ReservationRequest request) {


        Vol vol = volRepository.findById(request.volId())
                .orElseThrow(() -> new ResourceNotFoundException("Vol introuvable avec l'id " + request.volId()));
        if (vol.getCompagnie().getStatut() != StatutCompagnie.ACTIVE) {
            throw new BusinessException("La compagnie de ce vol n'est pas disponible");
        }
        verifierVolReservable(vol);
        List<PassagerRequest> passagersReq = request.passagers();
        verifierDoublonsDansLaRequete(passagersReq);

        int nb = passagersReq.size();
        if (!vol.estDisponible(nb)) {
            throw new BusinessException("Places insuffisantes : " + vol.getNbrePlacesDispo()
                    + " disponible(s), " + nb + " demandée(s)");
        }
        verifierSiegesEtPasseports(vol.getId(), passagersReq);

        LocalDateTime maintenant = LocalDateTime.now();
        Reservation reservation = new Reservation();
        reservation.setReference(genererReference());
        reservation.setUserId(CurrentUser.id());   // le voyageur, depuis le token
        reservation.setVol(vol);
        reservation.setStatutReservation(StatutReservation.EN_ATTENTE);
        reservation.setNbrePassagers(nb);
        reservation.setMontantTotal(vol.getPrix().multiply(BigDecimal.valueOf(nb)));
        reservation.setDateCreation(maintenant);
        reservation.setDateExpiration(maintenant.plusMinutes(dureeExpirationMinutes));

        for (PassagerRequest pr : passagersReq) {
            Passager passager = passagerMapper.toEntity(pr);
            passager.setReservation(reservation);
            reservation.getPassagers().add(passager);
        }

        vol.reserverPlaces(nb);          // décrémente nbrePlacesDispo
        volRepository.save(vol);         // @Version : échoue si un autre client a réservé entre-temps
        return mapper.toResponse(reservationRepository.save(reservation));
    }

    // ---------- Lecture ----------

    @Transactional(readOnly = true)
    public List<ReservationResponse> lireToutes(Long userId, Long volId, StatutReservation statut) {
        Long moi = CurrentUser.id();
        List<Reservation> base;
        if (CurrentUser.isAdmin()) {
            base = (userId != null) ? reservationRepository.findByUserId(userId) : reservationRepository.findAll();
        } else if (CurrentUser.isPartenaire()) {
            base = reservationRepository.findByVolCompagniePartenaireId(moi);   // réservations de SES vols
        } else {
            base = reservationRepository.findByUserId(moi);                     // voyageur : les siennes
        }
        return base.stream()
                .filter(r -> userId == null || r.getUserId().equals(userId))
                .filter(r -> volId == null || r.getVol().getId().equals(volId))
                .filter(r -> statut == null || r.getStatutReservation() == statut)
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<String> lireSiegesOccupes(Long volId) {
        if (!volRepository.existsById(volId)) {
            throw new ResourceNotFoundException("Vol introuvable avec l'id " + volId);
        }
        return passagerRepository.findSiegesOccupes(volId);
    }

    @Transactional(readOnly = true)
    public ReservationResponse lireParId(Long id) {
        Reservation r = trouver(id);
        verifierAcces(r);
        return mapper.toResponse(r);
    }

    @Transactional(readOnly = true)
    public ReservationResponse lireParReference(String reference) {
        String ref = reference.trim().toUpperCase();
        Reservation r = reservationRepository.findByReference(ref)
                .orElseThrow(() -> new ResourceNotFoundException("Réservation introuvable avec la référence " + ref));
        verifierAcces(r);
        return mapper.toResponse(r);
    }

    @Transactional(readOnly = true)
    public List<PassagerResponse> lirePassagers(Long reservationId) {
        Reservation r = trouver(reservationId);
        verifierAcces(r);
        return r.getPassagers().stream().map(passagerMapper::toResponse).toList();
    }

    // ---------- Modification d'un passager ----------

    @Transactional
    public PassagerResponse modifierPassager(Long reservationId, Long passagerId, PassagerRequest request) {
        Reservation r = trouver(reservationId);
        CurrentUser.exigerProprietaireOuAdmin(r.getUserId());
        if (!STATUTS_ACTIFS.contains(r.getStatutReservation())) {
            throw new BusinessException("Les passagers d'une réservation annulée ou expirée ne peuvent plus être modifiés");
        }
        verifierVolPasDejaParti(r);

        Passager passager = r.getPassagers().stream()
                .filter(p -> p.getId().equals(passagerId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Passager " + passagerId + " introuvable dans la réservation " + reservationId));

        Long volId = r.getVol().getId();

        // On ne contrôle que ce qui change (sinon le passager se bloquerait lui-même)
        String nouveauPasseport = normaliser(request.numPasseport());
        if (!nouveauPasseport.equalsIgnoreCase(passager.getNumPasseport())
                && passagerRepository.passeportDejaSurVol(volId, nouveauPasseport)) {
            throw new BusinessException("Le passeport " + nouveauPasseport + " figure déjà sur ce vol");
        }
        if (request.numeroSiege() != null) {
            String nouveauSiege = normaliser(request.numeroSiege());
            if (!nouveauSiege.equalsIgnoreCase(passager.getNumeroSiege())
                    && passagerRepository.siegeDejaOccupe(volId, nouveauSiege)) {
                throw new BusinessException("Le siège " + nouveauSiege + " est déjà occupé sur ce vol");
            }
        }

        passagerMapper.updateEntity(passager, request);
        return passagerMapper.toResponse(passagerRepository.save(passager));
    }

    // ---------- Cycle de vie ----------

    @Transactional
    public ReservationResponse confirmer(Long id, ConfirmationRequest request) {
        Reservation r = trouver(id);
        CurrentUser.exigerProprietaireOuAdmin(r.getUserId());
        if (r.getStatutReservation() != StatutReservation.EN_ATTENTE) {
            throw new BusinessException("Seule une réservation en attente peut être confirmée (statut actuel : "
                    + r.getStatutReservation() + ")");
        }
        if (!r.getDateExpiration().isAfter(LocalDateTime.now())) {
            throw new BusinessException("La réservation a expiré : elle ne peut plus être confirmée");
        }
        r.setPaiementId(request.paiementId());
        r.confirmer();
        return mapper.toResponse(reservationRepository.save(r));
    }

    @Transactional
    public ReservationResponse annuler(Long id) {
        Reservation r = trouver(id);
        CurrentUser.exigerProprietaireOuAdmin(r.getUserId());
        if (!STATUTS_ACTIFS.contains(r.getStatutReservation())) {
            throw new BusinessException("Seule une réservation en attente ou confirmée peut être annulée (statut actuel : "
                    + r.getStatutReservation() + ")");
        }
        verifierVolPasDejaParti(r);
        libererPlaces(r);
        r.annuler();
        // TODO (inter-services) : si CONFIRMEE, demander le remboursement à payment-service
        //                         et publier un événement pour notification-service
        return mapper.toResponse(reservationRepository.save(r));
    }

    @Transactional
    public ReservationResponse expirer(Long id) {
        Reservation r = trouver(id);
        if (r.getStatutReservation() != StatutReservation.EN_ATTENTE) {
            throw new BusinessException("Seule une réservation en attente peut expirer (statut actuel : "
                    + r.getStatutReservation() + ")");
        }
        expirerInterne(r);
        return mapper.toResponse(reservationRepository.save(r));
    }

    /** Appelée par le scheduler : expire toutes les réservations EN_ATTENTE échues. */
    @Transactional
    public int expirerReservationsEchues() {
        List<Reservation> echues = reservationRepository
                .findByStatutReservationAndDateExpirationBefore(StatutReservation.EN_ATTENTE, LocalDateTime.now());
        echues.forEach(this::expirerInterne);
        reservationRepository.saveAll(echues);
        return echues.size();
    }

    // ---------- Méthodes privées ----------

    private Reservation trouver(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Réservation introuvable avec l'id " + id));
    }

    private void expirerInterne(Reservation r) {
        libererPlaces(r);
        r.expirer();
    }

    private void libererPlaces(Reservation r) {
        Vol vol = r.getVol();
        vol.libererPlaces(r.getPassagers().size());
        volRepository.save(vol);
    }

    private void verifierVolReservable(Vol vol) {
        if (vol.getModeration() != StatutModeration.APPROUVE) {
            throw new BusinessException("Ce vol n'est pas ouvert à la réservation");
        }
        if (vol.getStatutVol() != StatutVol.PROGRAMME && vol.getStatutVol() != StatutVol.RETARD) {
            throw new BusinessException("Ce vol n'est plus réservable (statut : " + vol.getStatutVol() + ")");
        }
        if (!vol.getDateHeureDepart().isAfter(LocalDateTime.now())) {
            throw new BusinessException("Ce vol est déjà parti");
        }
    }

    private void verifierVolPasDejaParti(Reservation r) {
        if (!r.getVol().getDateHeureDepart().isAfter(LocalDateTime.now())) {
            throw new BusinessException("Le vol est déjà parti : opération impossible");
        }
    }

    private void verifierDoublonsDansLaRequete(List<PassagerRequest> passagers) {
        Set<String> passeports = new HashSet<>();
        Set<String> sieges = new HashSet<>();
        for (PassagerRequest p : passagers) {
            String passeport = normaliser(p.numPasseport());
            if (!passeports.add(passeport)) {
                throw new InvalidRequestException("Le passeport " + passeport + " apparaît plusieurs fois dans la réservation");
            }
            if (p.numeroSiege() != null) {
                String siege = normaliser(p.numeroSiege());
                if (!sieges.add(siege)) {
                    throw new InvalidRequestException("Le siège " + siege + " est attribué à plusieurs passagers de la réservation");
                }
            }
        }
    }
    /** Voyageur propriétaire, partenaire propriétaire du vol, ou admin. */
    private void verifierAcces(Reservation r) {
        if (CurrentUser.isAdmin()) return;
        Long moi = CurrentUser.id();
        boolean voyageur = moi.equals(r.getUserId());
        boolean partenaireDuVol = moi.equals(r.getVol().getCompagnie().getPartenaireId());
        if (!voyageur && !partenaireDuVol) {
            throw new ForbiddenException("Cette réservation ne vous appartient pas");
        }
    }

    private void verifierSiegesEtPasseports(Long volId, List<PassagerRequest> passagers) {
        for (PassagerRequest p : passagers) {
            String passeport = normaliser(p.numPasseport());
            if (passagerRepository.passeportDejaSurVol(volId, passeport)) {
                throw new BusinessException("Le passeport " + passeport + " figure déjà sur ce vol");
            }
            if (p.numeroSiege() != null) {
                String siege = normaliser(p.numeroSiege());
                if (passagerRepository.siegeDejaOccupe(volId, siege)) {
                    throw new BusinessException("Le siège " + siege + " est déjà occupé sur ce vol");
                }
            }
        }
    }

    private String genererReference() {
        String reference;
        do {
            reference = "RES-" + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE)
                    + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        } while (reservationRepository.existsByReference(reference));
        return reference;
    }

    private String normaliser(String valeur) {
        return valeur.trim().toUpperCase();
    }
}