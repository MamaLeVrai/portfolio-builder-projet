package alt.portfolio.builder.configurations;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import alt.portfolio.builder.entities.User;
import alt.portfolio.builder.utils.AuthUtils;

/**
 * Ajoute automatiquement des variables globales à tous les templates
 */
@ControllerAdvice
public class GlobalControllerAdvice {

	@ModelAttribute("currentUser")
	public User getCurrentUser() {
		return AuthUtils.getCurrentUser();
	}

	@ModelAttribute("isAdmin")
	public boolean isAdmin() {
		return AuthUtils.isAdmin();
	}
}
