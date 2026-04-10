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
 * Service pour l'authentification et les opérations de base de données des utilisateurs
 */
@Service
public class DbUserServices implements UserDetailsService {

	@Autowired
	private UserRepositories userRepositories;

	@Autowired
	private PasswordEncoder passwordEncoder;

	/**
	 * Charge un utilisateur par son nom d'utilisateur (utilisé par Spring Security)
	 */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<User> optUser = userRepositories.findByEmail(username);
		if (optUser.isEmpty()) {
			optUser = userRepositories.findByUsername(username);
		}
		return optUser.orElseThrow(() -> new UsernameNotFoundException("Utilisateur introuvable: " + username));
	}

	/**
	 * Encode le mot de passe d'un utilisateur
	 */
	public void encodePassword(User user) {
		user.setPassword(passwordEncoder.encode(user.getPassword()));
	}
}
