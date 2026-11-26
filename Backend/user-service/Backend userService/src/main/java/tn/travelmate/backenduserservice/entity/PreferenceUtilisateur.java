package tn.travelmate.backenduserservice.entity;

import jakarta.persistence.*;
import tn.travelmate.backenduserservice.ennum.TypeVoyage;

@Entity
@Table(name = "preference_utilisateur")
public class PreferenceUtilisateur {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "utilisateur_id", unique = true)
    private Utilisateur utilisateur;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_voyage_prefere", length = 20)
    private TypeVoyage typeVoyagePrefere;

    @Column(name = "destination_preferee") private String destinationPreferee;
    @Column(name = "langue_preferee")      private String languePreferee;
    @Column(name = "devise_preferee")      private String devisePreferee;
    @Column(name = "budget_min")           private Double budgetMin;
    @Column(name = "budget_max")           private Double budgetMax;
    @Column(name = "nombre_voyageurs_prefere") private Integer nombreVoyageursPrefere;

    protected PreferenceUtilisateur() {}
    public PreferenceUtilisateur(Utilisateur utilisateur) { this.utilisateur = utilisateur; }

    public Long getId() { return id; }
    public TypeVoyage getTypeVoyagePrefere() { return typeVoyagePrefere; }
    public void setTypeVoyagePrefere(TypeVoyage v) { this.typeVoyagePrefere = v; }
    public String getDestinationPreferee() { return destinationPreferee; }
    public void setDestinationPreferee(String v) { this.destinationPreferee = v; }
    public String getLanguePreferee() { return languePreferee; }
    public void setLanguePreferee(String v) { this.languePreferee = v; }
    public String getDevisePreferee() { return devisePreferee; }
    public void setDevisePreferee(String v) { this.devisePreferee = v; }
    public Double getBudgetMin() { return budgetMin; }
    public void setBudgetMin(Double v) { this.budgetMin = v; }
    public Double getBudgetMax() { return budgetMax; }
    public void setBudgetMax(Double v) { this.budgetMax = v; }
    public Integer getNombreVoyageursPrefere() { return nombreVoyageursPrefere; }
    public void setNombreVoyageursPrefere(Integer v) { this.nombreVoyageursPrefere = v; }
}
