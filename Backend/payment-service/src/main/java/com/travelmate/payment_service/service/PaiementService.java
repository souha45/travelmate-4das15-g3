package com.travelmate.payment_service.service;

import com.travelmate.payment_service.entity.Paiement;
import com.travelmate.payment_service.enums.StatutPaiement;
import com.travelmate.payment_service.repository.PaiementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaiementService {
    private final PaiementRepository repo;

    public List<Paiement> rechercher(Long utilisateurId, StatutPaiement statut) { return repo.rechercher(utilisateurId, statut); }

    public Paiement trouver(Long id) {
        return repo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Paiement introuvable"));
    }

    public Paiement trouverParReference(String reference) {
        return repo.findByReference(reference).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Paiement introuvable"));
    }

    @Transactional
    public Paiement creer(Paiement p) {
        p.setId(null);
        if (p.getReference() == null || p.getReference().isBlank()) {
            p.setReference("PAY-" + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE) + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }
        return repo.save(p);
    }

    @Transactional
    public Paiement modifier(Long id, Paiement data) {
        Paiement p = trouver(id);
        p.setReservationId(data.getReservationId());
        p.setReservationReference(data.getReservationReference());
        p.setTypeReservation(data.getTypeReservation());
        p.setUtilisateurId(data.getUtilisateurId());
        p.setMontant(data.getMontant());
        p.setDevise(data.getDevise());
        p.setMethodePaiement(data.getMethodePaiement());
        p.setStatut(data.getStatut() == null ? p.getStatut() : data.getStatut());
        p.setReferenceTransactionPasserelle(data.getReferenceTransactionPasserelle());
        p.setCleIdempotence(data.getCleIdempotence());
        p.setMotifEchec(data.getMotifEchec());
        return repo.save(p);
    }

    public void supprimer(Long id) {
        trouver(id);
        repo.deleteById(id);
    }
}