package tn.travelmate.backenduserservice.entity;

import jakarta.persistence.*;
import tn.travelmate.backenduserservice.ennum.TypePartenaire;

@Entity
@Table(name = "profil_partenaire")
public class ProfilPartenaire {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "utilisateur_id", unique = true)
    private Utilisateur utilisateur;

    @Column(name = "nom_societe", nullable = false)
    private String nomSociete;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_partenaire", nullable = false, length = 30)
    private TypePartenaire typePartenaire;

    @Column(name = "numero_fiscal")
    private String numeroFiscal;

    protected ProfilPartenaire() {}

    public ProfilPartenaire(Utilisateur utilisateur, String nomSociete, TypePartenaire type, String numeroFiscal) {
        this.utilisateur = utilisateur;
        this.nomSociete = nomSociete;
        this.typePartenaire = type;
        this.numeroFiscal = numeroFiscal;
    }

    public Long getId() { return id; }
    public String getNomSociete() { return nomSociete; }
    public TypePartenaire getTypePartenaire() { return typePartenaire; }
    public String getNumeroFiscal() { return numeroFiscal; }
    public void setNomSociete(String v) { this.nomSociete = v; }
    public void setTypePartenaire(TypePartenaire v) { this.typePartenaire = v; }
    public void setNumeroFiscal(String v) { this.numeroFiscal = v; }
}
