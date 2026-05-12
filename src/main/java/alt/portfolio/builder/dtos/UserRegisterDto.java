package alt.portfolio.builder.dtos;

import alt.portfolio.builder.entities.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO (Data Transfer Object) pour l'inscription d'un nouvel utilisateur.
 *
 * Transporte toutes les données du formulaire d'inscription avec leurs règles de validation.
 * Chaque champ a des annotations qui vérifient automatiquement les contraintes :
 * - @NotBlank : le champ ne peut pas être vide
 * - @Size : limite la taille minimum ou maximum
 * - @Email : vérifie que le format de l'email est valide (exemple@domaine.com)
 */
@Data
public class UserRegisterDto {

	/** Prénom (obligatoire, max 45 caractères) */
	@NotBlank(message = "Le prénom est obligatoire")
	@Size(max = 45, message = "Le prénom ne doit pas dépasser 45 caractères")
	private String firstname;

	/** Nom de famille (obligatoire, max 45 caractères) */
	@NotBlank(message = "Le nom est obligatoire")
	@Size(max = 45, message = "Le nom ne doit pas dépasser 45 caractères")
	private String lastname;

	/** Nom d'utilisateur unique pour se connecter (obligatoire, max 45 caractères) */
	@NotBlank(message = "Le nom d'utilisateur est obligatoire")
	@Size(max = 45, message = "Le nom d'utilisateur ne doit pas dépasser 45 caractères")
	private String username;

	/**
	 * Adresse email (obligatoire, doit être au format valide, max 45 caractères).
	 * @Email vérifie automatiquement le format : préfixe@domaine.extension
	 */
	@NotBlank(message = "L'email est obligatoire")
	@Email(message = "L'email doit être valide")
	@Size(max = 45, message = "L'email ne doit pas dépasser 45 caractères")
	private String email;

	/**
	 * Mot de passe (obligatoire, minimum 6 caractères).
	 * Ce mot de passe sera haché (encodé) avant d'être stocké en base de données.
	 */
	@NotBlank(message = "Le mot de passe est obligatoire")
	@Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères")
	private String password;

	/**
	 * Confirmation du mot de passe (doit être identique à password).
	 * La vérification est faite dans AuthController avant d'appeler UserService.
	 */
	@NotBlank(message = "La confirmation du mot de passe est obligatoire")
	private String confirmPassword;

	/**
	 * Convertit ce DTO en entité User, prête à être sauvegardée.
	 * Note : le mot de passe est copié tel quel ici,
	 * il sera encodé par DbUserServices.encodePassword() avant la sauvegarde.
	 *
	 * @param user L'entité User vide à remplir
	 * @return L'entité User avec les données du formulaire
	 */
	public User toUser(User user) {
		user.setFirstname(firstname);
		user.setLastname(lastname);
		user.setUsername(username);
		user.setEmail(email);
		user.setPassword(password); // Sera encodé par DbUserServices avant save()
		return user;
	}
}
