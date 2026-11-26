package tn.travelmate.backenduserservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import tn.travelmate.backenduserservice.ennum.NomRole;
import tn.travelmate.backenduserservice.entity.Role;
import tn.travelmate.backenduserservice.repository.RoleRepository;
import tn.travelmate.backenduserservice.service.UserService;

@Component
public class DataInitializer implements CommandLineRunner {
    private final RoleRepository roles;
    private final UserService users;
    private final String adminEmail;
    private final String adminPassword;

    public DataInitializer(RoleRepository roles, UserService users,
                           @Value("${app.admin.email}") String adminEmail,
                           @Value("${app.admin.password}") String adminPassword) {
        this.roles = roles;
        this.users = users;
        this.adminEmail = adminEmail;
        this.adminPassword = adminPassword;
    }

    @Override
    public void run(String... args) {
        creer(NomRole.VOYAGEUR, "Utilisateur qui planifie et réserve ses voyages");
        creer(NomRole.PARTENAIRE, "Compagnie aérienne, hébergeur ou loueur");
        creer(NomRole.ADMINISTRATEUR, "Responsable de la plateforme");
        users.creerAdministrateurSiAbsent(adminEmail, adminPassword);
    }

    private void creer(NomRole nom, String description) {
        if (roles.findByNom(nom).isEmpty()) {
            roles.save(new Role(nom, description));
        }
    }
}
