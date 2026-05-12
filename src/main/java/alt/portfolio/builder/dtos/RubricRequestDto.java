package alt.portfolio.builder.dtos;

import alt.portfolio.builder.entities.Rubric;
import lombok.Data;

/**
 * DTO (Data Transfer Object) pour les requêtes de création/modification de rubrique.
 *
 * Transporte les données du formulaire d'ajout ou de modification d'une rubrique.
 * order_ est le numéro d'ordre de la rubrique dans le profil (le "_" évite le conflit
 * avec le mot-clé SQL "ORDER").
 *
 * Note : l'ordre n'est pas appliqué dans toRubric() car il est calculé automatiquement
 * par DbRubricService lors de la création (le nouvel item prend la dernière position).
 */
@Data
public class RubricRequestDto {

	/** Nom (titre) de la rubrique à afficher sur le profil */
	private String name;

	/** Position souhaitée dans le profil (calculé automatiquement à la création) */
	private int order_;

	/**
	 * Convertit ce DTO en entité Rubric.
	 * L'ordre n'est pas copié ici car il est géré par le service.
	 *
	 * @param rubric L'entité Rubric à remplir
	 * @return La rubrique avec son nouveau nom
	 */
	public Rubric toRubric(Rubric rubric) {
		rubric.setName(name);
		// order_ non utilisé ici : l'ordre est géré automatiquement dans DbRubricService
		return rubric;
	}
}
