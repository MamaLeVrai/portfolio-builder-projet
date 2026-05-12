package alt.portfolio.builder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * C'est la porte d'entrée de toute l'application !
 *
 * @SpringBootApplication dit à Spring Boot : "Lance tout automatiquement".
 * Spring Boot va trouver tout seul tous les contrôleurs, services et configurations
 * qu'on a écrits dans les autres fichiers, et les connecter ensemble.
 */
@SpringBootApplication
public class Portfolio1Application {

	/**
	 * La méthode main est le tout premier code qui s'exécute quand on démarre le serveur.
	 * SpringApplication.run(...) démarre le serveur web et toute l'application Spring Boot.
	 */
	public static void main(String[] args) {
		SpringApplication.run(Portfolio1Application.class, args);
	}

}
