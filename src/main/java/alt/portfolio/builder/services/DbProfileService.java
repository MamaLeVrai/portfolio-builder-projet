package alt.portfolio.builder.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import alt.portfolio.builder.entities.Profile;
import alt.portfolio.builder.entities.User;
import alt.portfolio.builder.repositories.ProfileRepositories;

@Service
public class DbProfileService {

	@Autowired
	private ProfileRepositories profileRepo;

	public Profile createProfile(String name) {
		// récupérer l'utilisateur courant depuis Spring Security
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
			throw new IllegalStateException("Aucun utilisateur connecté, impossible de créer un profil");
		}
		User currentUser = (User) authentication.getPrincipal();

		Profile profile = new Profile();
		profile.setName(name);
		// définir le propriétaire obligatoire du profil
		profile.setOwner(currentUser);

		return profileRepo.save(profile);
	}
}