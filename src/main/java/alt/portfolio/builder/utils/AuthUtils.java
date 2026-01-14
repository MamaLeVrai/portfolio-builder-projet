package alt.portfolio.builder.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import alt.portfolio.builder.entities.User;

/**
 * Utilitaire pour la gestion de l'authentification
 */
public class AuthUtils {

	private AuthUtils() {
		// Classe utilitaire - constructeur privé
	}

	/**
	 * Récupère l'utilisateur actuellement authentifié
	 * @return L'utilisateur connecté ou null si non authentifié
	 */
	public static User getCurrentUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null && auth.getPrincipal() instanceof User) {
			return (User) auth.getPrincipal();
		}
		return null;
	}

	/**
	 * Vérifie si un utilisateur est authentifié
	 * @return true si authentifié, false sinon
	 */
	public static boolean isAuthenticated() {
		return getCurrentUser() != null;
	}

	/**
	 * Vérifie si l'utilisateur connecté est admin
	 * @return true si admin, false sinon
	 */
	public static boolean isAdmin() {
		User currentUser = getCurrentUser();
		return currentUser != null && "ADMIN".equals(currentUser.getRole());
	}

	/**
	 * Vérifie si l'utilisateur connecté est propriétaire de la ressource
	 * @param ownerId L'ID du propriétaire de la ressource
	 * @return true si propriétaire ou admin, false sinon
	 */
	public static boolean isOwnerOrAdmin(java.util.UUID ownerId) {
		User currentUser = getCurrentUser();
		if (currentUser == null) {
			return false;
		}
		return currentUser.getId().equals(ownerId) || isAdmin();
	}
}

