package alt.portfolio.builder.services;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import alt.portfolio.builder.dtos.UserRegisterDto;
import alt.portfolio.builder.dtos.UserRequestDto;
import alt.portfolio.builder.dtos.UserUpdateDto;
import alt.portfolio.builder.entities.User;
import alt.portfolio.builder.repositories.UserRepositories;

@Service
public class UserService {

	@Autowired
	private UserRepositories userRepositories;

	@Autowired
	private DbUserServices dbUserServices;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public List<User> getUsers() {
		return userRepositories.findByArchiverFalse();
	}

	public User createUser(UserRequestDto userRequest) {
		// vérification : email déjà utilisé ?
		userRepositories.findByEmail(userRequest.getEmail()).ifPresent(u -> {
			throw new IllegalArgumentException("Email déjà utilisé");
		});

		User user = userRequest.toUser(new User());
		dbUserServices.encodePassword(user);
		return userRepositories.save(user);
	}

	public User getUserById(UUID id) {
		return userRepositories.findById(id).orElseThrow(() -> new RuntimeException("Utilisateur introuvable: " + id));
	}

	// au lieu de supprimer physiquement, on archive
	public void archiveUser(UUID id) {
		User user = getUserById(id);
		user.setArchiver(true);
		userRepositories.save(user);
	}

	// Registration method for US-001
	public User registerUser(UserRegisterDto registerDto) {
		// Check if email already exists
		userRepositories.findByEmail(registerDto.getEmail()).ifPresent(u -> {
			throw new IllegalArgumentException("Cet email est déjà utilisé");
		});

		// Check if username already exists
		userRepositories.findByUsername(registerDto.getUsername()).ifPresent(u -> {
			throw new IllegalArgumentException("Ce nom d'utilisateur est déjà utilisé");
		});

		User user = registerDto.toUser(new User());
		dbUserServices.encodePassword(user);
		return userRepositories.save(user);
	}

	// Update user method for US-004
	public User updateUser(UUID userId, UserUpdateDto updateDto) {
		User user = getUserById(userId);

		// Check if email is being changed and if new email is already used
		if (!user.getEmail().equals(updateDto.getEmail())) {
			userRepositories.findByEmail(updateDto.getEmail()).ifPresent(u -> {
				if (!u.getId().equals(userId)) {
					throw new IllegalArgumentException("Cet email est déjà utilisé");
				}
			});
		}

		user = updateDto.updateUser(user);

		// Handle password change if provided
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

	// Delete account method for US-005
	public void deleteAccount(UUID userId) {
		User user = getUserById(userId);
		// Physical deletion (profiles will be cascade deleted due to orphanRemoval = true)
		userRepositories.delete(user);
	}
}