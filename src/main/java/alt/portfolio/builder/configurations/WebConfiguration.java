package alt.portfolio.builder.configurations;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration web de l'application.
 *
 * Cette classe permet de lier des URLs à des vues HTML directement,
 * sans avoir besoin d'écrire un contrôleur complet.
 */
@Configuration
public class WebConfiguration implements WebMvcConfigurer {

	/**
	 * Quand quelqu'un visite l'URL "/login" dans son navigateur,
	 * on lui affiche directement la page "users/formLogin.html".
	 * Pas besoin d'écrire une méthode de contrôleur pour ça !
	 */
	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/login").setViewName("/users/formLogin");
	}
}