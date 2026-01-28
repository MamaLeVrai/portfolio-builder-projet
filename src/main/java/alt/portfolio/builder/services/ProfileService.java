package alt.portfolio.builder.services;

import java.util.List;
import java.util.UUID;

import alt.portfolio.builder.exceptions.EntityNotFoundException;
import alt.portfolio.builder.exceptions.UnauthorizedException;
import alt.portfolio.builder.exceptions.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import alt.portfolio.builder.dtos.ProfileCreateDto;
import alt.portfolio.builder.dtos.ProfileRequestDto;
import alt.portfolio.builder.dtos.ProfileUpdateDto;
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

	// US-006: Create profile with new DTO
	public Profile createProfileNew(ProfileCreateDto createDto, User currentUser) {
		if (currentUser == null) {
			throw new IllegalArgumentException("Utilisateur non connecté");
		}

		Profile profile = createDto.toProfile(new Profile());
		profile.setOwner(currentUser);
		return profileRepositories.save(profile);
	}

	// US-007: Get profiles by user sorted by updated date
	public List<Profile> getProfilesByUserSorted(User user) {
		return profileRepositories.findByOwnerAndArchivedFalseOrderByUpdatedAtDesc(user);
	}

	// US-008: Update profile
	public Profile updateProfile(UUID profileId, ProfileUpdateDto updateDto, User currentUser) {
		Profile profile = getProfileById(profileId);

		// Check ownership
		if (!profile.getOwner().getId().equals(currentUser.getId())) {
			throw new IllegalArgumentException("Vous n'êtes pas autorisé à modifier ce profil");
		}

		profile = updateDto.updateProfile(profile);
		return profileRepositories.save(profile);
	}

	// US-009: Duplicate profile
	public Profile duplicateProfile(UUID profileId, User currentUser) {
		Profile original = getProfileById(profileId);

		// Check ownership
		if (!original.getOwner().getId().equals(currentUser.getId())) {
			throw new IllegalArgumentException("Vous n'êtes pas autorisé à dupliquer ce profil");
		}

		Profile duplicate = new Profile();
		duplicate.setName(original.getName() + " (Copie)");
		duplicate.setDescription(original.getDescription());
		duplicate.setImageUrl(original.getImageUrl());
		duplicate.setStatus("draft");
		duplicate.setDefault(false);
		duplicate.setOwner(original.getOwner());

		return profileRepositories.save(duplicate);
	}

	// US-010: Delete profile (with ownership check)
	public void deleteProfile(UUID profileId, User currentUser) {
		Profile profile = getProfileById(profileId);

		// Check ownership
		if (!profile.getOwner().getId().equals(currentUser.getId())) {
			throw new IllegalArgumentException("Vous n'êtes pas autorisé à supprimer ce profil");
		}

		// Archive instead of physical delete
		profile.setArchived(true);
		profileRepositories.save(profile);
	}

	// US-011: Set default profile
	public Profile setDefaultProfile(UUID profileId, User currentUser) throws EntityNotFoundException, UnauthorizedException {
		Profile profile = getProfileById(profileId);

		// Check ownership
		if (!profile.getOwner().getId().equals(currentUser.getId())) {
			throw new UnauthorizedException("Vous n'êtes pas autorisé à modifier ce profil");
		}

		// Remove default status from other profiles
		profileRepositories.findByOwnerAndIsDefaultTrue(currentUser).ifPresent(defaultProfile -> {
			defaultProfile.setDefault(false);
			profileRepositories.save(defaultProfile);
		});

		// Set this profile as default
		profile.setDefault(true);
		return profileRepositories.save(profile);
	}
}