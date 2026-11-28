package com.travelmate.payment_service.service;

import com.travelmate.payment_service.entity.Paiement;
import com.travelmate.payment_service.entity.Remboursement;
import com.travelmate.payment_service.repository.PaiementRepository;
import com.travelmate.payment_service.repository.RemboursementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RemboursementService {
    private final RemboursementRepository repo;
    private final PaiementRepository paiementRepo;

    public List<Remboursement> rechercher(Long paiementId) { return repo.rechercher(paiementId); }

    public Remboursement trouver(Long id) {
        return repo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Remboursement introuvable"));
    }

    @Transactional
    public Remboursement creer(Remboursement data) {
        Paiement p = resoudrePaiement(data.getPaiement());
        Remboursement r = new Remboursement();
        r.setPaiement(p);
        r.setMontantInitial(data.getMontantInitial() == null ? p.getMontant() : data.getMontantInitial());
        r.setTauxRetenue(data.getTauxRetenue());
        r.setMotif(data.getMotif());
        if (data.getStatut() != null) r.setStatut(data.getStatut());
        r.setReferenceRemboursementPasserelle(data.getReferenceRemboursementPasserelle());
        r.setDateCreation(LocalDateTime.now());
        calculer(r, p);
        return repo.save(r);
    }

    @Transactional
    public Remboursement modifier(Long id, Remboursement data) {
        Remboursement r = trouver(id);
        if (data.getMontantInitial() != null) r.setMontantInitial(data.getMontantInitial());
        r.setTauxRetenue(data.getTauxRetenue());
        r.setMotif(data.getMotif());
        if (data.getStatut() != null) r.setStatut(data.getStatut());
        r.setReferenceRemboursementPasserelle(data.getReferenceRemboursementPasserelle());
        calculer(r, r.getPaiement());
        return repo.save(r);
    }

    public void supprimer(Long id) {
        trouver(id);
        repo.deleteById(id);
    }

    private void calculer(Remboursement r, Paiement p) {
        if (r.getTauxRetenue() == null) r.setTauxRetenue(BigDecimal.ZERO);
        if (r.getMontantInitial().compareTo(p.getMontant()) > 0) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Montant initial supérieur au montant du paiement");
        BigDecimal retenu = r.getMontantInitial().multiply(r.getTauxRetenue()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal rembourse = r.getMontantInitial().subtract(retenu);
        if (rembourse.signum() <= 0) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Montant remboursé nul");
        r.setMontantRetenu(retenu);
        r.setMontantRembourse(rembourse);
    }

    private Paiement resoudrePaiement(Paiement ref) {
        if (ref == null || ref.getId() == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "paiement.id obligatoire");
        return paiementRepo.findById(ref.getId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Paiement introuvable"));
    }
}