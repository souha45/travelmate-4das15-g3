package com.travelmate.flight_service.scheduler;

import com.travelmate.flight_service.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationExpirationScheduler {

    private final ReservationService reservationService;

    @Scheduled(fixedDelayString = "${flight.reservation.verification-expiration-ms:60000}")
    public void expirerLesReservationsEchues() {
        try {
            int nombre = reservationService.expirerReservationsEchues();
            if (nombre > 0) {
                log.info("{} réservation(s) expirée(s) automatiquement", nombre);
            }
        } catch (Exception e) {
            // Ex : conflit de concurrence sur un vol. Le prochain passage réessaiera.
            log.warn("Échec de l'expiration automatique : {}", e.getMessage());
        }
    }
}