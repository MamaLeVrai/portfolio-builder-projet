package alt.portfolio.builder.dtos;

import alt.portfolio.builder.entities.User;
import lombok.Data;

/**
 * DTO (Data Transfer Object) pour les requêtes de suppression d'utilisateur.
 *
 * Ce DTO transporte l'identifiant de l'utilisateur à supprimer.
 * La méthode toUser() est présente mais ne fait que retourner l'utilisateur
 * sans modification (on a juste besoin de l'ID pour savoir qui supprimer).
 *
 * Note : la suppression de compte est gérée dans UserController.deleteAccount()
 * qui utilise directement l'utilisateur connecté sans passer par ce DTO.
 */
@Data
public class UserDeleteDto {

	/** Identifiant (UUID sous forme de String) de l'utilisateur à supprimer */
	private String Id;

	/**
	 * Retourne l'utilisateur tel quel.
	 * La suppression réelle est réalisée dans UserService.deleteAccount().
	 *
	 * @param user L'utilisateur à supprimer
	 * @return Le même utilisateur (non modifié)
	 */
	public User toUser(User user) {
		user.getId();
		return user;
	}
}
