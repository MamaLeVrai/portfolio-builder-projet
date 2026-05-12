package alt.portfolio.builder.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO (Data Transfer Object) pour la connexion d'un utilisateur.
 *
 * Transporte les données saisies dans le formulaire de connexion :
 * l'identifiant (nom d'utilisateur OU email) et le mot de passe.
 *
 * Note : Spring Security gère la vérification du mot de passe lui-même.
 * Ce DTO sert principalement à valider que les champs ne sont pas vides.
 */
@Data
public class UserLoginDto {

	/**
	 * Identifiant de connexion : peut être le nom d'utilisateur OU l'email.
	 * DbUserServices.loadUserByUsername() essaie les deux.
	 * @NotBlank : ce champ est obligatoire.
	 */
	@NotBlank(message = "Le nom d'utilisateur ou email est obligatoire")
	private String usernameOrEmail;

	/** Mot de passe saisi dans le formulaire (obligatoire) */
	@NotBlank(message = "Le mot de passe est obligatoire")
	private String password;
}
