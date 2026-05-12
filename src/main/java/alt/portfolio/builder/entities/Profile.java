package alt.portfolio.builder.entities;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

/**
 * Profile — La fiche de présentation d'un utilisateur.
 *
 * Pense à ça comme à une feuille de papier sur laquelle tu écris
 * tout sur toi : ton nom, ta description, tes expériences…
 * Un utilisateur peut avoir PLUSIEURS profils (un pour chercher un emploi,
 * un autre pour montrer ses projets, etc.).
 *
 * Modifié dans Epic 4 (publication) et Epic 5 (personnalisation visuelle).
 */
@Entity
@Getter
@Setter
public class Profile {

    /** Identifiant unique du profil, généré automatiquement */
    @Id
    private UUID id = UUID.randomUUID();

    /**
     * Indique si le profil est "archivé" (= caché, pas vraiment supprimé).
     * Quand tu "supprimes" un profil, on le cache juste au lieu de l'effacer
     * pour ne pas perdre les données.
     */
    private boolean archived = false;

    /** Le titre du profil (ex: "Mon CV développeur", "Portfolio créatif") */
    @Column(length = 150, nullable = false)
    private String name;

    /** Un texte de présentation qui apparaît sur le CV et le Portfolio */
    @Column(length = 500, nullable = false)
    private String description = "";

    /** L'adresse web (URL) de la photo de profil (ex: "/uploads/profiles/ma-photo.jpg") */
    @Column(length = 255, nullable = true)
    private String imageUrl;

    /**
     * Date de création du profil.
     * "updatable = false" signifie qu'on ne peut pas modifier cette date après création.
     * Remplie automatiquement par Hibernate.
     */
    @Column(nullable = true, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    /** Date de la dernière modification. Mise à jour automatiquement par Hibernate. */
    @Column(nullable = true)
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    /**
     * État du profil :
     * - "draft"     = brouillon (en cours de construction, pas encore visible)
     * - "published" = publié (visible par tout le monde)
     * - "archived"  = archivé (plus visible)
     */
    @Column(length = 20, nullable = false)
    private String status = "draft";

    /**
     * Est-ce que c'est le profil "principal" de l'utilisateur ?
     * Le profil par défaut est celui qui s'affiche en priorité sur les URLs publiques.
     */
    @Column(nullable = false)
    private boolean isDefault = false;

    /**
     * (Epic 4 - US-022) Est-ce que ce profil est publié en tant que CV ?
     * Si true, il est accessible à l'URL /public/cv/{username}.
     */
    @Column(nullable = false)
    private boolean publishedAsCv = false;

    /**
     * (Epic 4 - US-022) Est-ce que ce profil est publié en tant que Portfolio ?
     * Si true, il est accessible à l'URL /public/portfolio/{username}.
     */
    @Column(nullable = false)
    private boolean publishedAsPortfolio = false;

    /**
     * (Epic 4 - US-023) Nombre total de fois que ce profil a été vu.
     * Incrémenté à chaque visite d'un visiteur sur les pages publiques.
     */
    @Column(nullable = false)
    private long viewCount = 0;

    /**
     * (Epic 5 - US-025/028) La couleur principale choisie pour la vue CV.
     * Stockée en format hexadécimal (ex: "#2c3e50" = un bleu très foncé).
     * Valeur par défaut : bleu marine.
     */
    @Column(length = 7, nullable = false)
    private String cvColor = "#2c3e50";

    /**
     * (Epic 5 - US-025/028) La couleur principale choisie pour la vue Portfolio.
     * Valeur par défaut : violet-bleu.
     */
    @Column(length = 7, nullable = false)
    private String portfolioColor = "#667eea";

    /**
     * (Epic 6 - US-037) Slug personnalisé pour les URLs publiques.
     *
     * Le slug est un identifiant lisible choisi par l'utilisateur (ex: "mon-cv-dev").
     * Quand défini, les URLs publiques utilisent le slug au lieu du username :
     *   /public/cv/mon-cv-dev  au lieu de  /public/cv/jdupont
     *
     * Peut être null (non défini) : dans ce cas, on utilise le username comme avant.
     * Doit être unique parmi tous les profils.
     *
     * Choix d'implémentation : champ nullable unique sur Profile.
     * -- Alternative non retenue : modifier le Username de l'User (impact sur la connexion).
     * -- Alternative non retenue : slug au niveau User global (un slug par user, pas par profil).
     * -- Choix retenu : slug par profil, unique, null si non défini → rétrocompatible.
     */
    @Column(length = 100, unique = true, nullable = true)
    private String slug;

    /**
     * (Epic 5 - US-024) Le template (mise en page) choisi pour la vue CV.
     * Ex : "Classique", "Moderne", "Minimal", "Créatif".
     * Peut être null si aucun template n'a encore été choisi.
     */
    @ManyToOne(optional = true)
    private Template template;

    /**
     * (Epic 5 - US-024) Le template choisi pour la vue Portfolio.
     * Même chose que "template" mais pour le Portfolio.
     */
    @ManyToOne(optional = true)
    private Template template1;

    /**
     * Le propriétaire de ce profil (l'utilisateur qui l'a créé).
     * Un profil appartient toujours à un seul utilisateur.
     */
    @ManyToOne(optional = false)
    private User owner;

    /**
     * Les images associées à ce profil (table de liaison profile_image en base).
     * Relation plusieurs-à-plusieurs : un profil peut avoir plusieurs images,
     * et une image peut appartenir à plusieurs profils.
     */
    @ManyToMany
    @JoinTable(name = "profile_image",
        joinColumns = @JoinColumn(name = "profile_id"),
        inverseJoinColumns = @JoinColumn(name = "image_id"))
    private List<Image> images;

    /**
     * Les rubriques de ce profil (Expériences, Formations, Compétences…).
     * CascadeType.ALL = si on supprime le profil, toutes ses rubriques sont supprimées aussi.
     * orphanRemoval = si on retire une rubrique de la liste, elle est effacée de la base.
     */
    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Rubric> rubrics;
}
