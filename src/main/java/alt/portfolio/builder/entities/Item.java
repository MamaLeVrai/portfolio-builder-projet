package alt.portfolio.builder.entities;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Item — Un élément d'une rubrique (une expérience, un projet, une compétence…).
 *
 * Si une rubrique est une boîte (ex: "Mes expériences"),
 * un Item est un objet dans cette boîte (ex: "Stage chez Google, juin 2024").
 *
 * Modifié dans Epic 5 (US-031) : ajout du champ imageUrl
 * pour associer une image à un projet dans la vue Portfolio.
 */
@Entity
@Getter
@Setter
public class Item {

    /** Identifiant unique de cet item, généré automatiquement */
    @Id
    private UUID id = UUID.randomUUID();

    /** Le titre de l'item (ex: "Développeur Web chez Apple") */
    @Column(length = 150, nullable = false)
    private String title;

    /** Une description plus détaillée (peut être vide) */
    @Column(nullable = true, length = 1000)
    private String description;

    /** Date de début (ex: 2023-09-01 pour "1er septembre 2023") */
    private LocalDate startDate;

    /** Date de fin (peut être null si c'est encore en cours) */
    private LocalDate endDate;

    /**
     * Position dans la liste (pour trier les items dans l'ordre voulu).
     * 0 = premier, 1 = deuxième, etc.
     * Le nom "order_" est utilisé car "order" est un mot réservé en SQL.
     */
    @Column(name = "order_", nullable = false)
    private Byte order;

    /**
     * (Epic 5 - US-031) URL d'une image illustrant ce projet dans la vue Portfolio.
     * Ex: "/uploads/items/mon-projet.jpg"
     * Peut être null si aucune image n'a été ajoutée.
     */
    @Column(length = 255, nullable = true)
    private String imageUrl;

    /**
     * Le lieu où s'est déroulé cet item (optionnel).
     * Ex: "Paris", "Télétravail", etc.
     */
    @ManyToOne(optional = true)
    private Location location;

    /**
     * La rubrique à laquelle appartient cet item.
     * Un item appartient toujours à une seule rubrique.
     */
    @ManyToOne(optional = false)
    private Rubric rubric;
}
