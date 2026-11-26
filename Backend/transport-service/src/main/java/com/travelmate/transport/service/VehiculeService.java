package com.travelmate.transport.service;

import com.travelmate.transport.entity.Vehicule;
import com.travelmate.transport.repository.VehiculeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VehiculeService {

    private final VehiculeRepository vehiculeRepository;

    public List<Vehicule> findAll() {
        return vehiculeRepository.findAll();
    }

    public Optional<Vehicule> findById(String id) {
        return vehiculeRepository.findById(id);
    }

    public Vehicule save(Vehicule vehicule) {
        return vehiculeRepository.save(vehicule);
    }

    public Optional<Vehicule> update(String id, Vehicule details) {
        return vehiculeRepository.findById(id).map(v -> {
            v.setMarque(details.getMarque());
            v.setModele(details.getModele());
            v.setImmatriculation(details.getImmatriculation());
            v.setNombrePlaces(details.getNombrePlaces());
            v.setPrixParJour(details.getPrixParJour());
            v.setCategorie(details.getCategorie());
            v.setStatut(details.getStatut());
            return vehiculeRepository.save(v);
        });
    }

    public boolean deleteById(String id) {
        if (vehiculeRepository.existsById(id)) {
            vehiculeRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
