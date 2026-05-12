package alt.portfolio.builder.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import alt.portfolio.builder.entities.User;

/**
 * Dépôt (repository) pour accéder aux utilisateurs en base de données.
 *
 * En plus des opérations CRUD héritées de JpaRepository, ce repository a
 * des méthodes spéciales générées automatiquement par Spring Data JPA
 * grâce à la convention de nommage "findBy..." :
 *
 * Spring Data lit le nom de la méthode et génère automatiquement la requête SQL !
 * Par exemple, "findByUsername" génère : SELECT * FROM user WHERE username = ?
 */
@Repository
public interface UserRepositories extends JpaRepository<User, UUID> {

	/**
	 * Cherche un utilisateur par son nom d'utilisateur (username).
	 * Utilisé lors de la connexion pour trouver le compte correspondant.
	 * Retourne Optional<User> car il peut ne pas exister (Optional = "peut être vide").
	 */
	public Optional<User> findByUsername(String username);

	/**
	 * Récupère tous les utilisateurs qui NE sont PAS archivés (actifs).
	 * findByArchiverFalse → WHERE archiver = false en SQL.
	 * Utilisé pour afficher uniquement les utilisateurs actifs dans l'interface.
	 */
	public List<User> findByArchiverFalse();

	/**
	 * Cherche un utilisateur par son adresse email.
	 * Utilisé pour vérifier qu'un email n'est pas déjà utilisé lors de l'inscription,
	 * et aussi pour se connecter avec un email à la place du nom d'utilisateur.
	 */
	Optional<User> findByEmail(String email);
}