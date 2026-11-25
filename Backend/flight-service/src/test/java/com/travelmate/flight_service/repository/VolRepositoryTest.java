package com.travelmate.flight_service.repository;

import com.travelmate.flight_service.entity.*;
import com.travelmate.flight_service.enums.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
// Nouveau (Spring Boot 4) - À AJOUTER
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class VolRepositoryTest {

    @Autowired private VolRepository volRepository;
    @Autowired private CompagnieRepository compagnieRepository;
    @Autowired private AeroportRepository aeroportRepository;
    @Autowired private ReservationRepository reservationRepository;
    @Autowired private PassagerRepository passagerRepository;

    private Compagnie compagnie;
    private Aeroport tunis;
    private Aeroport paris;
    private Vol vol;

    @BeforeEach
    void init() {
        compagnie = new Compagnie();
        compagnie.setNomCompagnie("Tunisair");
        compagnie.setCodeCompagnie("TU");
        compagnie.setPaysCompagnie("Tunisie");
        compagnie = compagnieRepository.save(compagnie);

        tunis = new Aeroport();
        tunis.setCodeAeroport("TUN");
        tunis.setNomAeroport("Tunis-Carthage");
        tunis.setVille("Tunis");
        tunis.setPaysAeroport("Tunisie");
        tunis = aeroportRepository.save(tunis);

        paris = new Aeroport();
        paris.setCodeAeroport("CDG");
        paris.setNomAeroport("Charles de Gaulle");
        paris.setVille("Paris");
        paris.setPaysAeroport("France");
        paris = aeroportRepository.save(paris);

        vol = new Vol();
        vol.setNumeroVol("TU750");
        vol.setDateHeureDepart(LocalDateTime.of(2026, 12, 10, 8, 30));
        vol.setDateHeureArrivee(LocalDateTime.of(2026, 12, 10, 11, 0));
        vol.setPrix(new BigDecimal("350.00"));
        vol.setNbrePlacesTotal(100);
        vol.setNbrePlacesDispo(100);
        vol.setStatutVol(StatutVol.PROGRAMME);
        vol.setModeration(StatutModeration.APPROUVE);
        vol.setCompagnie(compagnie);
        vol.setAeroportDepart(tunis);
        vol.setAeroportArrivee(paris);
        vol = volRepository.save(vol);
    }

    @Test
    void findByCodeAeroport_doitRetournerAeroport() {
        assertThat(aeroportRepository.findByCodeAeroport("TUN")).isPresent();
        assertThat(aeroportRepository.existsByCodeAeroport("XXX")).isFalse();
    }

    @Test
    void findByNumeroVol_doitRetournerVol() {
        assertThat(volRepository.findByNumeroVol("TU750")).isPresent();
    }

    @Test
    void rechercherVols_doitTrouverLeVol() {
        LocalDate date = LocalDate.of(2026, 12, 10);
        List<Vol> resultat = volRepository.rechercherVols(
                "TUN", "CDG", date.atStartOfDay(), date.atTime(23, 59, 59), 2);
        assertThat(resultat).hasSize(1);
        assertThat(resultat.get(0).getNumeroVol()).isEqualTo("TU750");
    }

    @Test
    void rechercherVols_sansAssezDePlaces_doitRetournerVide() {
        LocalDate date = LocalDate.of(2026, 12, 10);
        List<Vol> resultat = volRepository.rechercherVols(
                "TUN", "CDG", date.atStartOfDay(), date.atTime(23, 59, 59), 500);
        assertThat(resultat).isEmpty();
    }

    @Test
    void rechercherVols_volNonApprouve_doitEtreExclu() {
        vol.setModeration(StatutModeration.EN_REVISION);
        volRepository.save(vol);
        LocalDate date = LocalDate.of(2026, 12, 10);
        List<Vol> resultat = volRepository.rechercherVols(
                "TUN", "CDG", date.atStartOfDay(), date.atTime(23, 59, 59), 1);
        assertThat(resultat).isEmpty();
    }

    @Test
    void siegeDejaOccupe_doitDetecterLeSiegePris() {
        Reservation r = new Reservation();
        r.setReference("RES-001");
        r.setUserId(1L);
        r.setStatutReservation(StatutReservation.CONFIRMEE);
        r.setMontantTotal(new BigDecimal("350.00"));
        r.setNbrePassagers(1);
        r.setDateCreation(LocalDateTime.now());
        r.setDateExpiration(LocalDateTime.now().plusMinutes(30));
        r.setVol(vol);

        Passager p = new Passager();
        p.setPrenom("Souha");
        p.setNom("Khenissi");
        p.setDateNaissance(LocalDate.of(2000, 1, 1));
        p.setNumPasseport("P1234567");
        p.setNationalite("Tunisienne");
        p.setNumeroSiege("12A");
        p.setReservation(r);
        r.getPassagers().add(p);

        reservationRepository.save(r);   // cascade ALL : le passager est sauvegardé aussi

        assertThat(passagerRepository.siegeDejaOccupe(vol.getId(), "12A")).isTrue();
        assertThat(passagerRepository.siegeDejaOccupe(vol.getId(), "12B")).isFalse();
        assertThat(reservationRepository.findByReference("RES-001")).isPresent();
        assertThat(reservationRepository.findByUserId(1L)).hasSize(1);
    }
}