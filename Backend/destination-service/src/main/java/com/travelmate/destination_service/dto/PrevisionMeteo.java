package com.travelmate.destination_service.dto;

import java.time.LocalDate;

public record PrevisionMeteo(LocalDate date, double tempMin, double tempMax,
                             String condition, double vent, Integer humidite) {}
