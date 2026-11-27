package com.travelmate.destination_service.dto;

import java.util.List;

public record MeteoReponse(String destinationId, String ville,
                           MeteoActuelle actuelle, List<PrevisionMeteo> previsions) {}
