package alt.portfolio.builder.dtos;

import java.util.UUID;

import alt.portfolio.builder.entities.Profile;
import lombok.Data;

/**
 * DTO (Data Transfer Object) pour les requêtes de création de profil (ancien système).
 *
 * Ce DTO est utilisé dans UserProfileController pour créer un profil via
 * la route /users/{userId}/profiles/create.
 *
 * Note : les champs sont nommés différemment de l'entité Profile :
 * - username → profile.name (le nom du profil)
 * - bio → profile.description (la description)
 * - avatarUrl → non utilisé ici (géré séparément via CustomizationController)
 */
@Data
public class ProfileRequestDto {

	/** Nom du profil (correspond à profile.name dans l'entité) */
	private String username;

	/** Description du profil (correspond à profile.description) */
	private String bio;

	/** URL de l'avatar (non utilisé dans toProfile pour l'instant) */
	private String avatarUrl;

	/** ID du propriétaire du profil (renseigné par le contrôleur, pas par le formulaire) */
	private UUID ownerId;

	/**
	 * Convertit ce DTO en entité Profile.
	 * Note : avatarUrl n'est pas copié ici, il est géré séparément.
	 *
	 * @param profile L'entité Profile vide à remplir
	 * @return Le Profile rempli
	 */
	public Profile toProfile(Profile profile) {
		profile.setName(username);
		profile.setDescription(bio != null ? bio : "");
		return profile;
	}
}