package alt.portfolio.builder.entities;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

/**
 * Entité qui représente une catégorie de rubrique.
 *
 * Une catégorie définit le "type" d'une rubrique et ses caractéristiques.
 * Exemples : "Expérience professionnelle" (avec dates), "Formation" (avec dates),
 * "Projet personnel" (avec lien), "Compétence" (sans dates ni lien).
 *
 * hasDates : si vrai, les items de cette catégorie pourront avoir une date de début/fin.
 * hasLink  : si vrai, les items pourront avoir un lien URL (ex : lien GitHub d'un projet).
 *
 * Les catégories sont créées par un admin et choisies lors de la création d'une rubrique.
 */
@Entity
@Getter @Setter
public class Category {

	/** Identifiant unique de la catégorie */
	@Id
	private UUID id = UUID.randomUUID();

	/** Nom affiché de la catégorie (ex : "Expérience professionnelle") */
	@Column(length = 50, nullable = false)
	private String name;

	/** Si vrai, les items de cette catégorie peuvent avoir des dates (début et fin) */
	@Column(nullable = false)
	private boolean hasDates;

	/** Si vrai, les items de cette catégorie peuvent avoir un lien web (URL) */
	@Column(nullable = false)
	private boolean hasLink;
}