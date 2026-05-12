package alt.portfolio.builder.configurations;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import alt.portfolio.builder.entities.User;
import alt.portfolio.builder.utils.AuthUtils;

/**
 * Cette classe ajoute automatiquement des informations utiles à TOUTES les pages HTML
 * de l'application, sans avoir à le faire dans chaque contrôleur.
 *
 * @ControllerAdvice signifie : "applique ça à tous les contrôleurs".
 * Grâce à ça, chaque template Mustache peut utiliser {{currentUser}} et {{isAdmin}}
 * pour savoir qui est connecté et s'il est administrateur.
 */
@ControllerAdvice
public class GlobalControllerAdvice {

	/**
	 * Récupère l'utilisateur actuellement connecté et le rend disponible dans tous les templates.
	 * Dans les fichiers HTML, on peut écrire {{currentUser.firstname}} pour afficher son prénom.
	 */
	@ModelAttribute("currentUser")
	public User getCurrentUser() {
		return AuthUtils.getCurrentUser();
	}

	/**
	 * Vérifie si l'utilisateur connecté est un administrateur.
	 * Dans les fichiers HTML, on peut écrire {{#isAdmin}} ... {{/isAdmin}}
	 * pour afficher du contenu uniquement aux admins.
	 */
	@ModelAttribute("isAdmin")
	public boolean isAdmin() {
		return AuthUtils.isAdmin();
	}
}
