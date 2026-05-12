package alt.portfolio.builder.entities;

import java.util.List;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Template — Un modèle de mise en page pour les profils.
 *
 * Imagine des pochoirs différents pour décorer un gâteau.
 * Chaque Template est un "pochoir" qui décide à quoi ressemble
 * le CV ou le Portfolio : classique, moderne, minimal ou créatif.
 *
 * Les 4 templates de base sont créés automatiquement au démarrage
 * par la classe TemplateInitializer.
 *
 * Modifié dans Epic 5 (US-024) : ajout du champ layoutKey.
 */
@Entity
@Getter
@Setter
public class Template {

    /** Identifiant unique du template, généré automatiquement */
    @Id
    private UUID id = UUID.randomUUID();

    /** Le nom du template affiché à l'utilisateur (ex: "Classique", "Moderne") */
    @Column(length = 50, nullable = false)
    private String name;

    /** Une courte description du style du template */
    @Column(nullable = true, length = 1000)
    private String description;

    /**
     * (Epic 5 - US-024) La clé technique du layout CSS.
     * Elle indique quelle classe CSS appliquer dans les pages HTML publiques.
     * Valeurs possibles : "classic", "modern", "minimal", "creative".
     * Correspond aux classes CSS ".layout-classic", ".layout-modern", etc.
     */
    @Column(length = 30, nullable = false)
    private String layoutKey = "classic";

    /**
     * Liste des profils qui utilisent ce template pour leur vue CV.
     * "mappedBy = template" indique que la relation est gérée du côté Profile.
     */
    @OneToMany(mappedBy = "template")
    private List<Profile> profiles;

    /**
     * Liste des profils qui utilisent ce template pour leur vue Portfolio.
     * "mappedBy = template1" correspond au champ "template1" dans Profile.
     */
    @OneToMany(mappedBy = "template1")
    private List<Profile> profiles1;
}
