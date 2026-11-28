package com.travelmate.payment_service.service;

import com.travelmate.payment_service.entity.Facture;
import com.travelmate.payment_service.entity.LigneFacture;
import com.travelmate.payment_service.entity.Paiement;
import com.travelmate.payment_service.repository.FactureRepository;
import com.travelmate.payment_service.repository.PaiementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FactureService {
    private final FactureRepository factureRepo;
    private final PaiementRepository paiementRepo;

    @Value("${payment.tva-rate:0.19}")
    private BigDecimal tvaRate;

    public List<Facture> rechercher(Long utilisateurId) {
        return utilisateurId == null ? factureRepo.findAll() : factureRepo.findByUtilisateur(utilisateurId);
    }

    public Facture trouver(Long id) {
        return factureRepo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Facture introuvable"));
    }

    @Transactional
    public Facture creer(Facture data) {
        Facture f = new Facture();
        f.setPaiement(resoudrePaiement(data.getPaiement()));
        f.setNumeroFacture(data.getNumeroFacture() != null && !data.getNumeroFacture().isBlank() ? data.getNumeroFacture() : "FAC-" + Year.now() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        f.setStatut(data.getStatut() == null ? f.getStatut() : data.getStatut());
        f.setDateEmission(data.getDateEmission() == null ? LocalDateTime.now() : data.getDateEmission());
        remplacerLignes(f, data.getLignes());
        calculerTotaux(f);
        return factureRepo.save(f);
    }

    @Transactional
    public Facture modifier(Long id, Facture data) {
        Facture f = trouver(id);
        if (data.getNumeroFacture() != null && !data.getNumeroFacture().isBlank()) f.setNumeroFacture(data.getNumeroFacture());
        if (data.getStatut() != null) f.setStatut(data.getStatut());
        if (data.getDateEmission() != null) f.setDateEmission(data.getDateEmission());
        remplacerLignes(f, data.getLignes());
        calculerTotaux(f);
        return factureRepo.save(f);
    }

    public void supprimer(Long id) {
        trouver(id);
        factureRepo.deleteById(id);
    }

    private void remplacerLignes(Facture f, List<LigneFacture> nouvelles) {
        f.getLignes().clear();
        if (nouvelles != null) {
            for (LigneFacture l : new ArrayList<>(nouvelles)) {
                LigneFacture copie = new LigneFacture();
                copie.setDesignation(l.getDesignation());
                copie.setQuantite(l.getQuantite());
                copie.setPrixUnitaire(l.getPrixUnitaire());
                f.ajouterLigne(copie);
            }
        }
    }

    private void calculerTotaux(Facture f) {
        BigDecimal ttc = f.getLignes().stream().map(LigneFacture::getSousTotal).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
        BigDecimal ht = ttc.divide(BigDecimal.ONE.add(tvaRate), 2, RoundingMode.HALF_UP);
        f.setMontantTtc(ttc);
        f.setMontantHt(ht);
        f.setMontantTva(ttc.subtract(ht));
    }

    private Paiement resoudrePaiement(Paiement ref) {
        if (ref == null || ref.getId() == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "paiement.id obligatoire");
        return paiementRepo.findById(ref.getId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Paiement introuvable"));
    }
}