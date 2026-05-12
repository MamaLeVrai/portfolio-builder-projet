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
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import alt.portfolio.builder.dtos.ProfileRequestDto;
import alt.portfolio.builder.entities.Profile;
import alt.portfolio.builder.entities.User;
import alt.portfolio.builder.services.ProfileService;
import alt.portfolio.builder.services.UserService;

/**
 * Contrôleur qui gère la liste des profils d'un utilisateur spécifique.
 *
 * Toutes les routes commencent par "/users/{userId}/profiles/".
 * {userId} est l'identifiant de l'utilisateur dont on veut voir les profils.
 *
 * Note : Ce contrôleur est l'ancien système de gestion des profils.
 * Le nouveau système principal se trouve dans ProfileController.
 */
@RequestMapping("/users/{userId}/profiles")
@Controller
public class UserProfileController {

	@Autowired
	private ProfileService profileService;

	@Autowired
	private UserService userService;

	/**
	 * Affiche la liste de tous les profils d'un utilisateur donné.
	 * URL : GET /users/{userId}/profiles
	 *
	 * canAddProfile est vrai seulement si c'est l'utilisateur lui-même qui consulte
	 * (on ne peut pas ajouter un profil à quelqu'un d'autre !).
	 */
	@GetMapping
	public ModelAndView list(@PathVariable UUID userId) {
		User user = userService.getUserById(userId);

		// Récupère l'utilisateur actuellement connecté
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UUID currentUserId = null;
		if (auth != null && auth.getPrincipal() instanceof User) {
			currentUserId = ((User) auth.getPrincipal()).getId();
		}
		// On peut ajouter un profil seulement si on visite sa propre page
		boolean canAddProfile = currentUserId != null && currentUserId.equals(userId);

		ModelAndView mv = new ModelAndView("/profiles/user-profiles");
		mv.addObject("profiles", profileService.getProfilesByUserId(userId));
		mv.addObject("user", user);
		mv.addObject("userId", userId);
		mv.addObject("canAddProfile", canAddProfile);
		return mv;
	}

	/**
	 * Affiche le formulaire de création d'un nouveau profil.
	 * URL : GET /users/{userId}/profiles/create
	 *
	 * Seul l'utilisateur propriétaire peut créer un profil.
	 * Si quelqu'un d'autre essaie, il est redirigé vers la liste des profils.
	 */
	@GetMapping("/create")
	public String createForm(@PathVariable UUID userId, ModelMap model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || !(auth.getPrincipal() instanceof User)) {
			return "redirect:/users/" + userId + "/profiles";
		}
		User currentUser = (User) auth.getPrincipal();
		// Vérification : c'est bien MON profil que je crée ?
		if (!userId.equals(currentUser.getId())) {
			return "redirect:/users/" + userId + "/profiles";
		}

		User user = userService.getUserById(userId);
		model.addAttribute("profile", new Profile());
		model.addAttribute("user", user);
		model.addAttribute("userId", userId);
		return "/profiles/create-form";
	}

	/**
	 * Enregistre le nouveau profil dans la base de données.
	 * URL : POST /users/{userId}/profiles/create
	 *
	 * Si la création échoue (ex : nom déjà pris), on réaffiche le formulaire avec l'erreur.
	 */
	@PostMapping("/create")
	public String create(@PathVariable UUID userId, @ModelAttribute ProfileRequestDto profileDto, ModelMap model) {
		try {
			profileDto.setOwnerId(userId);
			profileService.createProfile(profileDto);
			return "redirect:/users/" + userId + "/profiles";
		} catch (IllegalArgumentException e) {
			// En cas d'erreur, on réaffiche le formulaire avec les données saisies et le message d'erreur
			User user = userService.getUserById(userId);
			Profile p = new Profile();
			p.setName(profileDto.getUsername());
			model.addAttribute("profile", p);
			model.addAttribute("user", user);
			model.addAttribute("userId", userId);
			model.addAttribute("error", e.getMessage());
			return "/profiles/create-form";
		}
	}

	/**
	 * Affiche le détail d'un profil spécifique.
	 * URL : GET /users/{userId}/profiles/{profileId}
	 */
	@GetMapping("/{profileId}")
	public String show(@PathVariable UUID userId, @PathVariable UUID profileId, ModelMap model) {
		Profile profile = profileService.getProfileById(profileId);
		User user = userService.getUserById(userId);
		model.addAttribute("profile", profile);
		model.addAttribute("user", user);
		model.addAttribute("userId", userId);
		return "/profiles/show";
	}

	/**
	 * Archive un profil (soft delete : le profil n'est pas supprimé, juste masqué).
	 * URL : POST /users/{userId}/profiles/{profileId}/delete
	 * Redirige vers la liste des profils après archivage.
	 */
	@PostMapping("/{profileId}/delete")
	public RedirectView delete(@PathVariable UUID userId, @PathVariable UUID profileId) {
		profileService.archiveProfile(profileId);
		return new RedirectView("/users/" + userId + "/profiles");
	}
}
