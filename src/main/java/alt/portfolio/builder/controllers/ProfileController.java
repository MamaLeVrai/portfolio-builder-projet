package alt.portfolio.builder.controllers;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import alt.portfolio.builder.dtos.ProfileRequestDto;
import alt.portfolio.builder.entities.Profile;
import alt.portfolio.builder.entities.User;
import alt.portfolio.builder.services.ProfileService;

@RequestMapping("/profiles")
@Controller
public class ProfileController {

	@Autowired
	private ProfileService profileService;

	@GetMapping(path = { "", "/" })
	public ModelAndView index() {
		// [ATTENTION] Cette route affiche TOUS les profils de tous les utilisateurs
		// Elle ne devrait être utilisée que pour un admin ou une vue globale
		System.out.println("⚠️  ATTENTION: Route globale /profiles appelée - affiche TOUS les profils");
		return new ModelAndView("/profiles/index", "profiles", profileService.getProfiles());
	}

	// [Ajout] Formulaire création avec contrôle de propriétaire (ownerId)
	@GetMapping("/create")
	public Object create(@RequestParam(name = "ownerId", required = false) UUID ownerId, ModelMap model) {
		if (ownerId != null) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			if (auth == null || !(auth.getPrincipal() instanceof User)) {
				return new RedirectView("/users/" + ownerId + "/profiles");
			}
			User currentUser = (User) auth.getPrincipal();
			if (!ownerId.equals(currentUser.getId())) {
				return new RedirectView("/users/" + ownerId + "/profiles");
			}
		}
		model.addAttribute("profile", new Profile());
		if (ownerId != null) model.addAttribute("ownerId", ownerId);
		return "/profiles/profileform";
	}

	// [Ajout] Création de profil: redirection contextuelle vers la liste de l'utilisateur
	@PostMapping("/create")
	public String createProfile(@ModelAttribute ProfileRequestDto createdProfile, BindingResult bindingResult,
			ModelMap model) {
		try {
			System.out.println("🔵 POST /profiles/create - Création d'un profil");
			System.out.println("   ownerId reçu dans le DTO: " + createdProfile.getOwnerId());
			
			Profile profile = profileService.createProfile(createdProfile);
			
			// Toujours rediriger vers la liste des profils de l'utilisateur propriétaire
			UUID ownerId = profile.getOwner().getId();
			String redirectUrl = "redirect:/users/" + ownerId + "/profiles";
			
			System.out.println("   Profil créé avec owner: " + ownerId);
			System.out.println("   ✅ Redirection vers: " + redirectUrl);
			
			return redirectUrl;
		} catch (IllegalArgumentException e) {
			System.out.println("   ❌ Erreur lors de la création: " + e.getMessage());
			Profile p = new Profile();
			p.setName(createdProfile.getUsername());
			model.addAttribute("profile", p);
			if (createdProfile.getOwnerId() != null) model.addAttribute("ownerId", createdProfile.getOwnerId());
			return "/profiles/profileform";
		}
	}

	@GetMapping("/{id}")
	public String show(@PathVariable UUID id, ModelMap model) {
		Profile profile = profileService.getProfileById(id);
		model.addAttribute("profile", profile);
		return "/profiles/show";
	}

	// [Ajout] Suppression (archive) avec redirection vers la liste contextuelle si ownerId présent
	@PostMapping("/{id}/delete")
	public RedirectView delete(@PathVariable UUID id, @RequestParam(name = "ownerId", required = false) UUID ownerId) {
		profileService.archiveProfile(id);
		if (ownerId != null) {
			return new RedirectView("/users/" + ownerId + "/profiles");
		}
		return new RedirectView("/profiles");
	}
}