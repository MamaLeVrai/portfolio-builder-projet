package alt.portfolio.builder.entities;

import java.util.List;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Entité qui représente une rubrique (section) d'un profil.
 *
 * Une rubrique est une grande section du CV ou portfolio, comme :
 * - "Mes expériences professionnelles"
 * - "Ma formation"
 * - "Mes projets personnels"
 *
 * Chaque rubrique :
 * - appartient à UN profil (ManyToOne vers Profile)
 * - a UN type (sa catégorie : expérience, formation, projet...)
 * - peut être visible ou cachée sur le document public
 * - a un numéro d'ordre pour définir sa position sur la page
 * - contient une liste d'items (les détails concrets)
 */
@Entity
@Getter @Setter
public class Rubric {

	/** Identifiant unique de la rubrique */
	@Id
	private UUID id = UUID.randomUUID();

	/** Titre de la rubrique tel qu'il s'affichera sur le CV (ex : "Expériences") */
	@Column(length = 120, nullable = false)
	private String name;

	/**
	 * Position de la rubrique sur la page (1 = en haut, 2 = en dessous, etc.)
	 * Le nom SQL "order_" est utilisé car "order" est un mot réservé en SQL.
	 * Byte = un petit nombre entre -128 et 127 (suffisant pour l'ordre des rubriques)
	 */
	@Column(name = "order_", nullable = false)
	private Byte order;

	/**
	 * Si visible = true, la rubrique s'affiche sur le CV/portfolio public.
	 * Si visible = false, elle est cachée (utile pour préparer du contenu à l'avance).
	 * Par défaut, une rubrique est visible.
	 */
	@Column(nullable = false)
	private boolean visible = true;

	/**
	 * Le type de cette rubrique (expérience, formation, projet...).
	 * ManyToOne : plusieurs rubriques peuvent avoir la même catégorie.
	 * optional = false : une rubrique DOIT avoir une catégorie.
	 */
	@ManyToOne(optional = false)
	private Category category;

	/**
	 * Le profil auquel appartient cette rubrique.
	 * ManyToOne : un profil peut avoir plusieurs rubriques.
	 * optional = false : une rubrique DOIT appartenir à un profil.
	 */
	@ManyToOne(optional = false)
	private Profile profile;

	/**
	 * Liste des items (éléments détaillés) dans cette rubrique.
	 * CascadeType.ALL : si on supprime la rubrique, tous ses items sont supprimés aussi.
	 * orphanRemoval = true : un item sans rubrique est automatiquement supprimé.
	 */
	@OneToMany(mappedBy = "rubric", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Item> items;
}