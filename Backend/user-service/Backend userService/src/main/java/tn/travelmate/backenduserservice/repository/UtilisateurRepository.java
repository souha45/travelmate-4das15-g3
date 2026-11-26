package tn.travelmate.backenduserservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import tn.travelmate.backenduserservice.ennum.NomRole;
import tn.travelmate.backenduserservice.ennum.StatutCompte;
import tn.travelmate.backenduserservice.entity.Utilisateur;

import java.util.Optional;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {
    Optional<Utilisateur> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    // Recherche pour l'administrateur (filtres facultatifs sur le rôle et le statut)
    Page<Utilisateur> findByStatut(StatutCompte statut, Pageable pageable);

    Page<Utilisateur> findByRoles_Role_Nom(NomRole role, Pageable pageable);

    Page<Utilisateur> findByRoles_Role_NomAndStatut(NomRole role, StatutCompte statut, Pageable pageable);
}
