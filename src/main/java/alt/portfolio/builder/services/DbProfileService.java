package alt.portfolio.builder.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import alt.portfolio.builder.entities.Profile;
import alt.portfolio.builder.entities.User;
import alt.portfolio.builder.repositories.ProfileRepositories;

/**
 * Service de base de données basique pour créer des profils.
 *
 * Ce service contient la méthode de création de profil la plus simple :
 * il récupère l'utilisateur connecté et lui crée un profil avec un nom donné.
 *
 * Note : la plupart des opérations sur les profils sont gérées dans ProfileService
 * (le service principal) qui est plus complet. Ce service DbProfileService
 * est le service "historique" de base.
 */
@Service
public class DbProfileService {

	@Autowired
	private ProfileRepositories profileRepo;

	/**
	 * Crée un nouveau profil appartenant à l'utilisateur actuellement connecté.
	 *
	 * On récupère l'utilisateur connecté depuis Spring Security (le SecurityContext),
	 * puis on crée un profil avec le nom donné en paramètre.
	 *
	 * @param name Le nom du nouveau profil
	 * @return Le profil créé et sauvegardé en base de données
	 * @throws IllegalStateException Si personne n'est connecté (ne devrait pas arriver en production)
	 */
	public Profile createProfile(String name) {
		// Récupère l'utilisateur connecté depuis Spring Security
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
			throw new IllegalStateException("Aucun utilisateur connecté, impossible de créer un profil");
		}
		User currentUser = (User) authentication.getPrincipal();

		// Crée le profil et lui attribue son propriétaire
		Profile profile = new Profile();
		profile.setName(name);
		profile.setOwner(currentUser);

		return profileRepo.save(profile);
	}
}