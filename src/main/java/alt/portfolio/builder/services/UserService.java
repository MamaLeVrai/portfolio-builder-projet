package alt.portfolio.builder.services;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import alt.portfolio.builder.dtos.UserRegisterDto;
import alt.portfolio.builder.dtos.UserUpdateDto;
import alt.portfolio.builder.entities.User;
import alt.portfolio.builder.repositories.UserRepositories;

/**
 * Service de gestion des utilisateurs
 */
@Service
public class UserService {

	@Autowired
	private UserRepositories userRepositories;

	@Autowired
	private DbUserServices dbUserServices;

	@Autowired
	private PasswordEncoder passwordEncoder;

	/**
	 * Récupère tous les utilisateurs non archivés
	 */
	public List<User> getUsers() {
		return userRepositories.findByArchiverFalse();
	}

	/**
	 * Récupère tous les utilisateurs (y compris archivés) - Admin uniquement
	 */
	public List<User> getAllUsers() {
		return userRepositories.findAll();
	}

	/**
	 * Récupère un utilisateur par son ID
	 */
	public User getUserById(UUID id) {
		return userRepositories.findById(id)
			.orElseThrow(() -> new RuntimeException("Utilisateur introuvable: " + id));
	}

	/**
	 * Archive un utilisateur (soft delete)
	 */
	public void archiveUser(UUID id) {
		User user = getUserById(id);
		user.setArchiver(true);
		userRepositories.save(user);
	}

	/**
	 * US-001: Enregistre un nouvel utilisateur
	 */
	public User registerUser(UserRegisterDto registerDto) {
		// Vérification email
		userRepositories.findByEmail(registerDto.getEmail()).ifPresent(u -> {
			throw new IllegalArgumentException("Cet email est déjà utilisé");
		});

		// Vérification username
		userRepositories.findByUsername(registerDto.getUsername()).ifPresent(u -> {
			throw new IllegalArgumentException("Ce nom d'utilisateur est déjà utilisé");
		});

		User user = registerDto.toUser(new User());
		dbUserServices.encodePassword(user);
		return userRepositories.save(user);
	}

	/**
	 * US-004: Met à jour un utilisateur
	 */
	public User updateUser(UUID userId, UserUpdateDto updateDto) {
		User user = getUserById(userId);

		// Vérification email si modifié
		if (!user.getEmail().equals(updateDto.getEmail())) {
			userRepositories.findByEmail(updateDto.getEmail()).ifPresent(u -> {
				if (!u.getId().equals(userId)) {
					throw new IllegalArgumentException("Cet email est déjà utilisé");
				}
			});
		}

		user = updateDto.updateUser(user);

		// Gestion du changement de mot de passe
		if (updateDto.getNewPassword() != null && !updateDto.getNewPassword().isEmpty()) {
			if (!passwordEncoder.matches(updateDto.getCurrentPassword(), user.getPassword())) {
				throw new IllegalArgumentException("Le mot de passe actuel est incorrect");
			}
			if (!updateDto.getNewPassword().equals(updateDto.getConfirmNewPassword())) {
				throw new IllegalArgumentException("Les nouveaux mots de passe ne correspondent pas");
			}
			user.setPassword(passwordEncoder.encode(updateDto.getNewPassword()));
		}

		return userRepositories.save(user);
	}

	/**
	 * Met à jour un utilisateur (admin)
	 */
	public void updateUser(User user) {
		userRepositories.save(user);
	}

	/**
	 * US-005: Supprime le compte d'un utilisateur
	 */
	public void deleteAccount(UUID userId) {
		User user = getUserById(userId);
		userRepositories.delete(user);
	}

	/**
	 * Supprime un utilisateur (admin)
	 */
	public void deleteUser(UUID userId) {
		userRepositories.deleteById(userId);
	}
}