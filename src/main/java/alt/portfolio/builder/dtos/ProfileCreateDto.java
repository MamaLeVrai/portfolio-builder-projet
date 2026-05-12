package alt.portfolio.builder.dtos;

import alt.portfolio.builder.entities.Profile;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO (Data Transfer Object) pour la création d'un nouveau profil.
 *
 * Ce DTO transporte les données saisies dans le formulaire de création de profil.
 * @Data (Lombok) génère automatiquement getters, setters, equals, hashCode et toString.
 *
 * Les annotations de validation (@NotBlank, @Size) vérifient les données
 * avant même d'atteindre le code Java.
 */
@Data
public class ProfileCreateDto {

	/**
	 * Nom du profil (obligatoire, max 150 caractères).
	 * @NotBlank vérifie que le champ n'est pas vide ou rempli d'espaces.
	 */
	@NotBlank(message = "Le nom du profil est obligatoire")
	@Size(max = 150, message = "Le nom ne doit pas dépasser 150 caractères")
	private String name;

	/** Description du profil (optionnelle, max 500 caractères) */
	@Size(max = 500, message = "La description ne doit pas dépasser 500 caractères")
	private String description = "";

	/** URL de l'image du profil (optionnelle, max 255 caractères) */
	@Size(max = 255, message = "L'URL de l'image ne doit pas dépasser 255 caractères")
	private String imageUrl;

	/**
	 * Convertit ce DTO en entité Profile, prête à être sauvegardée en base de données.
	 * Le statut est initialisé à "draft" (brouillon) et isDefault à false.
	 *
	 * @param profile L'entité Profile vide à remplir
	 * @return Le Profile rempli avec les données du DTO
	 */
	public Profile toProfile(Profile profile) {
		profile.setName(name);
		profile.setDescription(description != null ? description : "");
		profile.setImageUrl(imageUrl);
		profile.setStatus("draft"); // Un nouveau profil commence toujours en brouillon
		profile.setDefault(false);  // Un nouveau profil n'est pas le profil par défaut
		return profile;
	}
}
