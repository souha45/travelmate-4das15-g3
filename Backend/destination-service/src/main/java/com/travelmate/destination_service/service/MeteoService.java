package com.travelmate.destination_service.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.travelmate.destination_service.dto.MeteoActuelle;
import com.travelmate.destination_service.dto.MeteoReponse;
import com.travelmate.destination_service.dto.PrevisionMeteo;
import com.travelmate.destination_service.exception.ApiException;
import com.travelmate.destination_service.model.Coordonnees;
import com.travelmate.destination_service.model.Destination;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class MeteoService {

    private static final Duration DUREE_CACHE = Duration.ofMinutes(30);

    private final DestinationService destinationService;
    private final RestClient restClient;
    private final Map<String, Entree> cache = new ConcurrentHashMap<>();

    private record Entree(MeteoReponse valeur, Instant date) {}

    public MeteoReponse obtenir(String destinationId) {
        Destination destination = destinationService.obtenirActive(destinationId);
        if (destination.getCoordonnees() == null) {
            throw new ApiException(HttpStatus.SERVICE_UNAVAILABLE, "Coordonnées de la destination manquantes.");
        }
        Entree entree = cache.get(destinationId);
        if (entree != null && entree.date().plus(DUREE_CACHE).isAfter(Instant.now())) {
            return entree.valeur();
        }
        try {
            MeteoReponse meteo = appeler(destination);
            cache.put(destinationId, new Entree(meteo, Instant.now()));
            return meteo;
        } catch (RuntimeException e) {
            if (entree != null) {
                return entree.valeur();   // météo périmée plutôt que rien
            }
            throw new ApiException(HttpStatus.SERVICE_UNAVAILABLE, "Météo indisponible pour le moment.");
        }
    }

    private MeteoReponse appeler(Destination d) {
        Coordonnees c = d.getCoordonnees();
        String url = UriComponentsBuilder.fromUriString("https://api.open-meteo.com/v1/forecast")
                .queryParam("latitude", c.getLatitude())
                .queryParam("longitude", c.getLongitude())
                .queryParam("current", "temperature_2m,relative_humidity_2m,wind_speed_10m,weather_code")
                .queryParam("daily", "temperature_2m_max,temperature_2m_min,wind_speed_10m_max,weather_code")
                .queryParam("forecast_days", 7)
                .queryParam("timezone", "auto")
                .build().toUriString();

        JsonNode racine = restClient.get()
                .uri(java.net.URI.create(url))
                .retrieve()
                .body(JsonNode.class);
        JsonNode courant = racine.get("current");
        JsonNode jours = racine.get("daily");

        MeteoActuelle actuelle = new MeteoActuelle(
                courant.get("temperature_2m").asDouble(),
                condition(courant.get("weather_code").asInt()),
                courant.get("wind_speed_10m").asDouble(),
                courant.get("relative_humidity_2m").asInt());

        List<PrevisionMeteo> previsions = new ArrayList<>();
        for (int i = 0; i < jours.get("time").size(); i++) {
            previsions.add(new PrevisionMeteo(
                    LocalDate.parse(jours.get("time").get(i).asText()),
                    jours.get("temperature_2m_min").get(i).asDouble(),
                    jours.get("temperature_2m_max").get(i).asDouble(),
                    condition(jours.get("weather_code").get(i).asInt()),
                    jours.get("wind_speed_10m_max").get(i).asDouble(),
                    null));
        }
        return new MeteoReponse(d.getId(), d.getNom(), actuelle, previsions);
    }

    /** Traduit le code météo (WMO) en texte français. */
    private String condition(int code) {
        return switch (code) {
            case 0 -> "Ciel dégagé";
            case 1, 2 -> "Peu nuageux";
            case 3 -> "Nuageux";
            case 45, 48 -> "Brouillard";
            case 51, 53, 55, 56, 57 -> "Bruine";
            case 61, 63, 65, 66, 67 -> "Pluie";
            case 71, 73, 75, 77 -> "Neige";
            case 80, 81, 82 -> "Averses";
            case 85, 86 -> "Averses de neige";
            case 95, 96, 99 -> "Orage";
            default -> "Variable";
        };
    }
}
