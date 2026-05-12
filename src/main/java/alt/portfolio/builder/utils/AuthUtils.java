package alt.portfolio.builder.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import alt.portfolio.builder.entities.User;

/**
 * Classe utilitaire pour tout ce qui concerne l'authentification.
 *
 * Cette classe fournit des méthodes "statiques" (on peut les appeler sans créer d'objet)
 * pour savoir qui est connecté et quels sont ses droits.
 *
 * Utilisée partout dans les contrôleurs pour vérifier les permissions.
 * Le constructeur est privé pour éviter qu'on crée des instances : ces méthodes
 * sont pensées pour être appelées directement comme AuthUtils.getCurrentUser().
 */
public class AuthUtils {

	/** Constructeur privé : cette classe ne doit jamais être instanciée */
	private AuthUtils() {
		// Classe utilitaire - constructeur privé
	}

	/**
	 * Récupère l'utilisateur actuellement connecté.
	 *
	 * Spring Security stocke l'utilisateur connecté dans le "SecurityContext",
	 * une sorte de boîte qui contient les infos de session. On va y lire le "principal"
	 * (= l'objet qui représente l'utilisateur connecté).
	 *
	 * @return L'objet User si quelqu'un est connecté, ou null si personne n'est connecté
	 */
	public static User getCurrentUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null && auth.getPrincipal() instanceof User) {
			return (User) auth.getPrincipal();
		}
		return null;
	}

	/**
	 * Vérifie si quelqu'un est connecté.
	 * @return true si un utilisateur est connecté, false si personne
	 */
	public static boolean isAuthenticated() {
		return getCurrentUser() != null;
	}

	/**
	 * Vérifie si l'utilisateur connecté est administrateur.
	 * Un admin a le rôle "ADMIN" en base de données.
	 * @return true si l'utilisateur connecté est admin, false sinon
	 */
	public static boolean isAdmin() {
		User currentUser = getCurrentUser();
		return currentUser != null && "ADMIN".equals(currentUser.getRole());
	}

	/**
	 * Vérifie si l'utilisateur connecté est soit le propriétaire d'une ressource,
	 * soit un administrateur.
	 *
	 * Utile pour vérifier "est-ce que j'ai le droit de voir/modifier ça ?"
	 * Un admin peut tout voir et modifier, un utilisateur normal seulement ses propres données.
	 *
	 * @param ownerId L'ID de l'utilisateur propriétaire de la ressource
	 * @return true si c'est le propriétaire ou un admin, false sinon
	 */
	public static boolean isOwnerOrAdmin(java.util.UUID ownerId) {
		User currentUser = getCurrentUser();
		if (currentUser == null) {
			return false;
		}
		// C'est bon si c'est moi (mon ID = ownerId) OU si je suis admin
		return currentUser.getId().equals(ownerId) || isAdmin();
	}
}

