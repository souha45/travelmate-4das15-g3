package tn.travelmate.backenduserservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.travelmate.backenduserservice.entity.PreferenceUtilisateur;

public interface PreferenceUtilisateurRepository extends JpaRepository<PreferenceUtilisateur, Long> {
}
