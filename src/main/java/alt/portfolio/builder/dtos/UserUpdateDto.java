package alt.portfolio.builder.dtos;

import alt.portfolio.builder.entities.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO (Data Transfer Object) pour la modification du compte utilisateur.
 *
 * Transporte les nouvelles valeurs du formulaire de modification de compte.
 * Permet de modifier le prénom, nom et email, et optionnellement le mot de passe.
 *
 * Pour changer le mot de passe, l'utilisateur doit renseigner :
 * 1. Son mot de passe actuel (currentPassword) pour prouver son identité
 * 2. Son nouveau mot de passe (newPassword)
 * 3. La confirmation du nouveau mot de passe (confirmNewPassword)
 */
@Data
public class UserUpdateDto {

	/** Nouveau prénom (obligatoire, max 45 caractères) */
	@NotBlank(message = "Le prénom est obligatoire")
	@Size(max = 45, message = "Le prénom ne doit pas dépasser 45 caractères")
	private String firstname;

	/** Nouveau nom de famille (obligatoire, max 45 caractères) */
	@NotBlank(message = "Le nom est obligatoire")
	@Size(max = 45, message = "Le nom ne doit pas dépasser 45 caractères")
	private String lastname;

	/** Nouvelle adresse email (obligatoire, format valide, max 45 caractères) */
	@NotBlank(message = "L'email est obligatoire")
	@Email(message = "L'email doit être valide")
	@Size(max = 45, message = "L'email ne doit pas dépasser 45 caractères")
	private String email;

	/**
	 * Nouveau mot de passe (optionnel, min 6 caractères si renseigné).
	 * Si ce champ est null ou vide, le mot de passe ne change pas.
	 */
	@Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères")
	private String newPassword;

	/** Confirmation du nouveau mot de passe (doit être identique à newPassword) */
	private String confirmNewPassword;

	/**
	 * Mot de passe actuel de l'utilisateur.
	 * Requis pour confirmer l'identité avant de changer le mot de passe.
	 */
	private String currentPassword;

	/**
	 * Applique les nouvelles valeurs (prénom, nom, email) sur l'entité User.
	 * Le changement de mot de passe est géré séparément dans UserService.updateUser().
	 *
	 * @param user L'entité User à mettre à jour
	 * @return L'entité User avec les nouvelles valeurs
	 */
	public User updateUser(User user) {
		user.setFirstname(firstname);
		user.setLastname(lastname);
		user.setEmail(email);
		return user;
	}
}
