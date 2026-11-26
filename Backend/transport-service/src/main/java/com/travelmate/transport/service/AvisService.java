package com.travelmate.transport.service;

import com.travelmate.transport.entity.Avis;
import com.travelmate.transport.repository.AvisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AvisService {

    private final AvisRepository avisRepository;

    public List<Avis> findAll() {
        return avisRepository.findAll();
    }

    public Optional<Avis> findById(String id) {
        return avisRepository.findById(id);
    }

    public Avis save(Avis avis) {
        return avisRepository.save(avis);
    }

    public Optional<Avis> update(String id, Avis details) {
        return avisRepository.findById(id).map(a -> {
            a.setCommentaire(details.getCommentaire());
            a.setNote(details.getNote());
            a.setVehiculeId(details.getVehiculeId());
            return avisRepository.save(a);
        });
    }

    public boolean deleteById(String id) {
        if (avisRepository.existsById(id)) {
            avisRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
