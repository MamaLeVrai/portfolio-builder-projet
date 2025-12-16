package alt.portfolio.builder.services;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import alt.portfolio.builder.dtos.userRequestDto;
import alt.portfolio.builder.entities.User;
import alt.portfolio.builder.repositories.UserRepositories;

@Service
public class UserService {

	@Autowired
	private UserRepositories userRepositories;

	@Autowired
	private DbUserServices dbUserServices;

	public List<User> getUsers() {
		return userRepositories.findByArchiverFalse();
	}

	public User createUser(userRequestDto userRequest) {
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
}