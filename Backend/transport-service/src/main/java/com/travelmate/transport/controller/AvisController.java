package com.travelmate.transport.controller;

import com.travelmate.transport.entity.Avis;
import com.travelmate.transport.service.AvisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/avis")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AvisController {

    private final AvisService avisService;

    @GetMapping
    public List<Avis> getAllAvis() {
        return avisService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Avis> getAvisById(@PathVariable String id) {
        return avisService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Avis createAvis(@RequestBody Avis avis) {
        return avisService.save(avis);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Avis> updateAvis(@PathVariable String id,
                                           @RequestBody Avis avisDetails) {
        return avisService.update(id, avisDetails)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAvis(@PathVariable String id) {
        return avisService.deleteById(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
