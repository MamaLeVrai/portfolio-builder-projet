package alt.portfolio.builder.services;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import alt.portfolio.builder.dtos.ProfileRequestDto;
import alt.portfolio.builder.entities.Profile;
import alt.portfolio.builder.repositories.ProfileRepositories;

@Service
public class ProfileService {

	@Autowired
	private ProfileRepositories profileRepositories;

	public List<Profile> getProfiles() {
		return profileRepositories.findByArchivedFalse();
	}

	public Profile createProfile(ProfileRequestDto request) {
		// vérification : username déjà utilisé ?
		profileRepositories.findByName(request.getUsername()).ifPresent(u -> {
			throw new IllegalArgumentException("Username déjà utilisé");
		});

		Profile profile = request.toProfile(new Profile());
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