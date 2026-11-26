package tn.travelmate.backenduserservice.entity;

import jakarta.persistence.*;
import tn.travelmate.backenduserservice.ennum.NomRole;
import tn.travelmate.backenduserservice.ennum.StatutCompte;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "utilisateur")
public class Utilisateur {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false) private String prenom;
    @Column(nullable = false) private String nom;
    @Column(nullable = false, unique = true) private String email;

    @Column(name = "mot_de_passe", nullable = false)
    private String motDePasse;

    private String telephone;
    @Column(name = "photo_profil") private String photoProfil;
    private String nationalite;
    @Column(name = "date_naissance") private LocalDate dateNaissance;
    private String adresse;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutCompte statut = StatutCompte.ACTIF;

    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    @Column(name = "date_modification", nullable = false)
    private LocalDateTime dateModification;

    @OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<UtilisateurRole> roles = new ArrayList<>();

    @OneToOne(mappedBy = "utilisateur", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private PreferenceUtilisateur preference;

    @OneToOne(mappedBy = "utilisateur", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private ProfilPartenaire profilPartenaire;

    protected Utilisateur() {}

    public Utilisateur(String prenom, String nom, String email, String motDePasseChiffre) {
        this.prenom = prenom;
        this.nom = nom;
        this.email = email;
        this.motDePasse = motDePasseChiffre;
    }

    @PrePersist void avantCreation() { dateCreation = dateModification = LocalDateTime.now(); }
    @PreUpdate  void avantModification() { dateModification = LocalDateTime.now(); }

    public void ajouterRole(Role role) { roles.add(new UtilisateurRole(this, role)); }

    public void retirerRole(NomRole nom) {
        roles.removeIf(ur -> ur.getRole().getNom() == nom);
    }

    public boolean aRole(NomRole nom) {
        return roles.stream().anyMatch(ur -> ur.getRole().getNom() == nom);
    }

    public void attacherPartenaire(ProfilPartenaire p) { this.profilPartenaire = p; }
    public void attacherPreference(PreferenceUtilisateur p) { this.preference = p; }
    public void detacherPartenaire() { this.profilPartenaire = null; }
    public void detacherPreference() { this.preference = null; }

    public Long getId() { return id; }
    public String getPrenom() { return prenom; }
    public void setPrenom(String v) { this.prenom = v; }
    public String getNom() { return nom; }
    public void setNom(String v) { this.nom = v; }
    public String getEmail() { return email; }
    public String getMotDePasse() { return motDePasse; }
    public void setMotDePasse(String v) { this.motDePasse = v; }
    public String getTelephone() { return telephone; }
    public void setTelephone(String v) { this.telephone = v; }
    public String getPhotoProfil() { return photoProfil; }
    public void setPhotoProfil(String v) { this.photoProfil = v; }
    public String getNationalite() { return nationalite; }
    public void setNationalite(String v) { this.nationalite = v; }
    public LocalDate getDateNaissance() { return dateNaissance; }
    public void setDateNaissance(LocalDate v) { this.dateNaissance = v; }
    public String getAdresse() { return adresse; }
    public void setAdresse(String v) { this.adresse = v; }
    public StatutCompte getStatut() { return statut; }
    public void setStatut(StatutCompte v) { this.statut = v; }
    public LocalDateTime getDateCreation() { return dateCreation; }
    public LocalDateTime getDateModification() { return dateModification; }
    public List<UtilisateurRole> getRoles() { return roles; }
    public PreferenceUtilisateur getPreference() { return preference; }
    public ProfilPartenaire getProfilPartenaire() { return profilPartenaire; }
}
