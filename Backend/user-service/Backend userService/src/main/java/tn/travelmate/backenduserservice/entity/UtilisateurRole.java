package tn.travelmate.backenduserservice.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "utilisateur_role")
public class UtilisateurRole {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur utilisateur;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "role_id")
    private Role role;

    @Column(name = "date_attribution", nullable = false)
    private LocalDateTime dateAttribution = LocalDateTime.now();

    protected UtilisateurRole() {}

    public UtilisateurRole(Utilisateur utilisateur, Role role) {
        this.utilisateur = utilisateur;
        this.role = role;
    }

    public Long getId() { return id; }
    public Role getRole() { return role; }
    public LocalDateTime getDateAttribution() { return dateAttribution; }
}
