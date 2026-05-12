package alt.portfolio.builder.services;

import java.util.List;
import java.util.UUID;

import alt.portfolio.builder.exceptions.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import alt.portfolio.builder.dtos.UserRegisterDto;
import alt.portfolio.builder.dtos.UserUpdateDto;
import alt.portfolio.builder.entities.User;
import alt.portfolio.builder.repositories.UserRepositories;

/**
 * Service principal pour gérer les comptes utilisateurs.
 *
 * Ce service fournit toutes les opérations sur les utilisateurs :
 * inscription, mise à jour du profil, changement de mot de passe,
 * suppression de compte, et gestion admin des utilisateurs.
 *
 * Il est utilisé par AuthController (inscription), UserController (modifier son compte),
 * et AdminController (gérer tous les utilisateurs).
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
	 * Récupère la liste des utilisateurs actifs (non archivés).
	 * Utilisé pour afficher les utilisateurs visibles dans l'application.
	 */
	public List<User> getUsers() {
		return userRepositories.findByArchiverFalse();
	}

	/**
	 * Récupère TOUS les utilisateurs, y compris les archivés.
	 * Réservé aux administrateurs pour voir l'historique complet.
	 */
	public List<User> getAllUsers() {
		return userRepositories.findAll();
	}

	/**
	 * Récupère un utilisateur par son identifiant unique.
	 * Lance une RuntimeException si l'utilisateur n'existe pas.
	 *
	 * @param id L'identifiant (UUID) de l'utilisateur recherché
	 * @return L'utilisateur trouvé
	 */
	public User getUserById(UUID id) {
		return userRepositories.findById(id)
			.orElseThrow(() -> new RuntimeException("Utilisateur introuvable: " + id));
	}

	/**
	 * Archive un utilisateur (soft delete) : il n'est plus actif mais reste en base.
	 * Un utilisateur archivé ne peut plus se connecter (isEnabled() retourne false).
	 *
	 * @param id L'identifiant de l'utilisateur à archiver
	 */
	public void archiveUser(UUID id) {
		User user = getUserById(id);
		user.setArchiver(true);
		userRepositories.save(user);
	}

	/**
	 * US-001 : Crée un nouveau compte utilisateur lors de l'inscription.
	 *
	 * Étapes :
	 * 1. Vérifie que l'email n'est pas déjà utilisé
	 * 2. Vérifie que le nom d'utilisateur n'est pas déjà pris
	 * 3. Convertit le DTO en entité User (via registerDto.toUser)
	 * 4. Encode le mot de passe (jamais stocker en clair !)
	 * 5. Sauvegarde l'utilisateur en base de données
	 *
	 * @param registerDto Les données du formulaire d'inscription
	 * @return Le nouvel utilisateur créé
	 */
	public User registerUser(UserRegisterDto registerDto) {
		// Vérification que l'email n'est pas déjà utilisé par quelqu'un d'autre
		userRepositories.findByEmail(registerDto.getEmail()).ifPresent(u -> {
			throw new IllegalArgumentException("Cet email est déjà utilisé");
		});

		// Vérification que le nom d'utilisateur n'est pas déjà pris
		userRepositories.findByUsername(registerDto.getUsername()).ifPresent(u -> {
			throw new IllegalArgumentException("Ce nom d'utilisateur est déjà utilisé");
		});

		// Création et sauvegarde de l'utilisateur
		User user = registerDto.toUser(new User());
		dbUserServices.encodePassword(user); // Le mot de passe est haché ici
		return userRepositories.save(user);
	}

	/**
	 * US-004 : Met à jour les informations du compte utilisateur.
	 *
	 * Gère aussi le changement de mot de passe si newPassword est renseigné :
	 * - Vérifie que l'ancien mot de passe est correct
	 * - Vérifie que le nouveau mot de passe est confirmé
	 * - Encode et sauvegarde le nouveau mot de passe
	 *
	 * @param userId    L'identifiant de l'utilisateur à mettre à jour
	 * @param updateDto Les nouvelles valeurs envoyées par le formulaire
	 * @return L'utilisateur mis à jour
	 */
	public User updateUser(UUID userId, UserUpdateDto updateDto) {
		User user = getUserById(userId);

		// Si l'email change, on vérifie qu'il n'est pas déjà utilisé par quelqu'un d'autre
		if (!user.getEmail().equals(updateDto.getEmail())) {
			userRepositories.findByEmail(updateDto.getEmail()).ifPresent(u -> {
				if (!u.getId().equals(userId)) {
					throw new IllegalArgumentException("Cet email est déjà utilisé");
				}
			});
		}

		// Applique les nouvelles valeurs (prénom, nom, email)
		user = updateDto.updateUser(user);

		// Gestion du changement de mot de passe (optionnel)
		if (updateDto.getNewPassword() != null && !updateDto.getNewPassword().isEmpty()) {
			// Vérifie que l'utilisateur connaît bien son mot de passe actuel
			if (!passwordEncoder.matches(updateDto.getCurrentPassword(), user.getPassword())) {
				throw new IllegalArgumentException("Le mot de passe actuel est incorrect");
			}
			// Vérifie que le nouveau mot de passe est identique à sa confirmation
			if (!updateDto.getNewPassword().equals(updateDto.getConfirmNewPassword())) {
				throw new IllegalArgumentException("Les nouveaux mots de passe ne correspondent pas");
			}
			// Encode et sauvegarde le nouveau mot de passe
			user.setPassword(passwordEncoder.encode(updateDto.getNewPassword()));
		}

		return userRepositories.save(user);
	}

	/**
	 * Met à jour directement un utilisateur (version admin, sans vérifications).
	 * Utilisé par AdminController pour modifier n'importe quel compte.
	 *
	 * @param user L'utilisateur avec les nouvelles valeurs à sauvegarder
	 */
	public void updateUser(User user) {
		userRepositories.save(user);
	}

	/**
	 * US-005 : Supprime définitivement le compte de l'utilisateur connecté.
	 * Contrairement à archiveUser(), ici l'utilisateur est vraiment effacé de la base.
	 *
	 * @param userId L'identifiant du compte à supprimer
	 */
	public void deleteAccount(UUID userId) throws EntityNotFoundException {
		User user = getUserById(userId);
		userRepositories.delete(user);
	}

	/**
	 * Supprime définitivement un utilisateur (version admin).
	 * Vérifie d'abord que l'utilisateur existe, puis le supprime.
	 *
	 * @param userId L'identifiant de l'utilisateur à supprimer
	 * @throws EntityNotFoundException Si l'utilisateur n'existe pas
	 */
	public void deleteUser(UUID userId) throws EntityNotFoundException {
		if (!userRepositories.existsById(userId)) {
			throw new EntityNotFoundException("Utilisateur introuvable: " + userId);
		}
		userRepositories.deleteById(userId);
	}
}