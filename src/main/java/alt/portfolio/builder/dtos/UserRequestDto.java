package alt.portfolio.builder.dtos;

import alt.portfolio.builder.entities.User;
import lombok.Data;

/**
 * DTO (Data Transfer Object) générique pour les informations d'un utilisateur.
 *
 * Ce DTO sert à transporter les données de base d'un utilisateur
 * (prénom, nom, username, email, mot de passe) sans les contraintes de validation
 * qui sont présentes dans UserRegisterDto et UserUpdateDto.
 *
 * Il peut être utilisé dans des contextes où les données sont déjà validées ailleurs.
 */
@Data
public class UserRequestDto {

	/** Prénom de l'utilisateur */
	private String firstname;

	/** Nom de famille de l'utilisateur */
	private String lastname;

	/** Nom d'utilisateur (identifiant de connexion) */
	private String username;

	/** Adresse email de l'utilisateur */
	private String email;

	/** Mot de passe (en clair, à encoder avant sauvegarde) */
	private String password;

	/**
	 * Convertit ce DTO en entité User.
	 * Le mot de passe doit être encodé séparément avant la sauvegarde en base.
	 *
	 * @param user L'entité User à remplir
	 * @return L'entité User avec les nouvelles valeurs
	 */
	public User toUser(User user) {
		user.setFirstname(firstname);
		user.setLastname(lastname);
		user.setUsername(username);
		user.setEmail(email);
		user.setPassword(password);
		return user;
	}
}