package tn.travelmate.backenduserservice.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import tn.travelmate.backenduserservice.ennum.NomRole;
import tn.travelmate.backenduserservice.entity.Role;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByNom(NomRole nom);
}
