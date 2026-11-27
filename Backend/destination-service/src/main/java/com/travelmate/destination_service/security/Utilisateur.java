package com.travelmate.destination_service.security;

import java.util.List;

public record Utilisateur(String id, List<String> roles) {
    public boolean estAdmin() { return roles.contains("ADMINISTRATEUR"); }
}