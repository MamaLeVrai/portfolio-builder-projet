package alt.portfolio.builder.services;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import alt.portfolio.builder.dtos.ProfileRequestDto;
import alt.portfolio.builder.entities.Profile;
import alt.portfolio.builder.entities.User;
import alt.portfolio.builder.repositories.ProfileRepositories;
import alt.portfolio.builder.repositories.UserRepositories;

@Service
public class ProfileService {

	@Autowired
	private ProfileRepositories profileRepositories;

	@Autowired
	private UserRepositories userRepositories;

	public List<Profile> getProfiles() {
		return profileRepositories.findByArchivedFalse();
	}

	public List<Profile> getProfilesByUserId(UUID userId) {
		return profileRepositories.findByOwnerIdAndArchivedFalse(userId);
	}

	public Profile createProfile(ProfileRequestDto request) {
		// vérification : username déjà utilisé ?
		profileRepositories.findByName(request.getUsername()).ifPresent(u -> {
			throw new IllegalArgumentException("Username déjà utilisé");
		});

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User currentUser = (authentication != null && authentication.getPrincipal() instanceof User)
				? (User) authentication.getPrincipal()
				: null;

		// Si un ownerId explicite est demandé, il doit correspondre à l'utilisateur connecté
		if (request.getOwnerId() != null) {
			if (currentUser == null || !request.getOwnerId().equals(currentUser.getId())) {
				throw new IllegalArgumentException("Action non autorisée: propriétaire différent de l'utilisateur connecté");
			}
		}

		Profile profile = request.toProfile(new Profile());

		// Déterminer le propriétaire (owner) obligatoire: ownerId sinon utilisateur connecté
		User owner = null;
		if (request.getOwnerId() != null) {
			owner = userRepositories.findById(request.getOwnerId())
					.orElseThrow(() -> new IllegalArgumentException("Utilisateur propriétaire introuvable"));
		} else {
			if (currentUser != null) {
				owner = currentUser;
			}
		}

		if (owner == null) {
			throw new IllegalArgumentException("Aucun propriétaire fourni ou connecté");
		}

		profile.setOwner(owner);
		return profileRepositories.save(profile);
	}

	public Profile getProfileById(UUID id) {
		return profileRepositories.findById(id).orElseThrow(() -> new RuntimeException("Profile introuvable: " + id));
	}

	// au lieu de supprimer physiquement, on archive
	public void archiveProfile(UUID id) {
		Profile profile = getProfileById(id);
		profile.setArchived(true);
		profileRepositories.save(profile);
	}
}