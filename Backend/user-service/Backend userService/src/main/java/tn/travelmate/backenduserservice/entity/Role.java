package tn.travelmate.backenduserservice.entity;

import jakarta.persistence.*;
import tn.travelmate.backenduserservice.ennum.NomRole;

@Entity
@Table(name = "role")
public class Role {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true, length = 30)
    private NomRole nom;

    private String description;

    protected Role() {}

    public Role(NomRole nom, String description) {
        this.nom = nom;
        this.description = description;
    }

    public Long getId() { return id; }
    public NomRole getNom() { return nom; }
    public String getDescription() { return description; }
}
