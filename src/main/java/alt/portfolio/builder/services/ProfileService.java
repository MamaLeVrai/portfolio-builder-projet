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
	
	@Autowired
	private DbProfileService dbProfileServices;
	
	public List<Profile> getProfiles(){
		return profileRepositories.findByUsername(); 
	}
	
	public Profile createProfile(ProfileRequestDto request) {
		// vérification : username déjà utilisé ?
		profileRepositories.findByUsername(request.getUsername())
			.ifPresent(u -> { throw new IllegalArgumentException("Username déjà utilisé"); });
	
		
		Profile profile = request.toProfile(new Profile());
		return profileRepositories.save(profile);
	}
	
	public Profile getProfileById(UUID id) {
		return profileRepositories.findById(id)
			.orElseThrow(() -> new RuntimeException("Profile introuvable: " + id));
	}
}
