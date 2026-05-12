package alt.portfolio.builder.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import alt.portfolio.builder.entities.User;
import alt.portfolio.builder.repositories.UserRepositories;

/**
 * Service utilisé par Spring Security pour l'authentification des utilisateurs.
 *
 * Ce service implémente UserDetailsService, une interface de Spring Security.
 * Quand quelqu'un essaie de se connecter, Spring Security appelle la méthode
 * loadUserByUsername() pour retrouver l'utilisateur en base de données.
 *
 * Ce service gère aussi l'encodage des mots de passe pour ne jamais
 * stocker un mot de passe en clair dans la base de données.
 */
@Service
public class DbUserServices implements UserDetailsService {

	@Autowired
	private UserRepositories userRepositories;

	@Autowired
	private PasswordEncoder passwordEncoder;

	/**
	 * Méthode appelée automatiquement par Spring Security lors de la connexion.
	 *
	 * On accepte à la fois le nom d'utilisateur ET l'email pour se connecter :
	 * 1. On cherche d'abord par email
	 * 2. Si on ne trouve rien, on cherche par username
	 * 3. Si on ne trouve toujours rien, on lance une exception "utilisateur introuvable"
	 *
	 * @param username Le texte saisi dans le champ "identifiant" du formulaire de connexion
	 * @return L'objet UserDetails (notre User) si trouvé
	 * @throws UsernameNotFoundException Si aucun utilisateur ne correspond
	 */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// Essai par email en premier (l'email est souvent plus facile à retenir)
		Optional<User> optUser = userRepositories.findByEmail(username);
		if (optUser.isEmpty()) {
			// Si pas trouvé par email, on essaie par nom d'utilisateur
			optUser = userRepositories.findByUsername(username);
		}
		return optUser.orElseThrow(() -> new UsernameNotFoundException("Utilisateur introuvable: " + username));
	}

	/**
	 * Encode le mot de passe d'un utilisateur avec BCrypt avant de le sauvegarder.
	 *
	 * BCrypt transforme le mot de passe en une chaîne illisible appelée "hash".
	 * Ex : "monMotDePasse" → "$2a$10$XdeFGhIjKlMnOpQrStUvW..." (impossible à décoder)
	 * Ainsi, même si la base de données est piratée, les mots de passe sont protégés.
	 *
	 * @param user L'utilisateur dont on veut encoder le mot de passe
	 */
	public void encodePassword(User user) {
		user.setPassword(passwordEncoder.encode(user.getPassword()));
	}
}
