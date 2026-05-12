package alt.portfolio.builder.dtos;

import alt.portfolio.builder.entities.Profile;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO (Data Transfer Object) pour la modification d'un profil existant.
 *
 * Ce DTO transporte les nouvelles valeurs du formulaire d'édition de profil.
 * Il permet de modifier le nom, la description, l'image et le statut.
 *
 * Les annotations @NotBlank et @Size vérifient automatiquement les données
 * avant qu'elles atteignent le code Java (Bean Validation).
 */
@Data
public class ProfileUpdateDto {

	/**
	 * Nouveau nom du profil (obligatoire, max 150 caractères).
	 * @NotBlank : le champ ne peut pas être vide.
	 */
	@NotBlank(message = "Le nom du profil est obligatoire")
	@Size(max = 150, message = "Le nom ne doit pas dépasser 150 caractères")
	private String name;

	/** Nouvelle description (optionnelle, max 500 caractères) */
	@Size(max = 500, message = "La description ne doit pas dépasser 500 caractères")
	private String description;

	/** Nouvelle URL de l'image (optionnelle, max 255 caractères) */
	@Size(max = 255, message = "L'URL de l'image ne doit pas dépasser 255 caractères")
	private String imageUrl;

	/** Nouveau statut du profil (ex : "draft", "published") */
	@Size(max = 20, message = "Le statut ne doit pas dépasser 20 caractères")
	private String status;

	/**
	 * Applique les nouvelles valeurs sur un profil existant.
	 * Le statut n'est mis à jour que s'il est renseigné (il peut être null).
	 *
	 * @param profile Le profil existant à mettre à jour
	 * @return Le profil modifié (mais pas encore sauvegardé en base de données)
	 */
	public Profile updateProfile(Profile profile) {
		profile.setName(name);
		profile.setDescription(description != null ? description : "");
		profile.setImageUrl(imageUrl);
		// On ne met le statut à jour que s'il est fourni
		if (status != null && !status.isEmpty()) {
			profile.setStatus(status);
		}
		return profile;
	}
}
