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

@RequestMapping("/users/{userId}/profiles")
@Controller
public class UserProfileController {

	@Autowired
	private ProfileService profileService;

	@Autowired
	private UserService userService;

	// Liste des profils d'un utilisateur
	@GetMapping
	public ModelAndView list(@PathVariable UUID userId) {
		User user = userService.getUserById(userId);

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UUID currentUserId = null;
		if (auth != null && auth.getPrincipal() instanceof User) {
			currentUserId = ((User) auth.getPrincipal()).getId();
		}
		boolean canAddProfile = currentUserId != null && currentUserId.equals(userId);

		ModelAndView mv = new ModelAndView("/profiles/user-profiles");
		mv.addObject("profiles", profileService.getProfilesByUserId(userId));
		mv.addObject("user", user);
		mv.addObject("userId", userId);
		mv.addObject("canAddProfile", canAddProfile);
		return mv;
	}

	// Formulaire de création d'un profil
	@GetMapping("/create")
	public String createForm(@PathVariable UUID userId, ModelMap model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || !(auth.getPrincipal() instanceof User)) {
			return "redirect:/users/" + userId + "/profiles";
		}
		User currentUser = (User) auth.getPrincipal();
		if (!userId.equals(currentUser.getId())) {
			return "redirect:/users/" + userId + "/profiles";
		}

		User user = userService.getUserById(userId);
		model.addAttribute("profile", new Profile());
		model.addAttribute("user", user);
		model.addAttribute("userId", userId);
		return "/profiles/create-form";
	}

	// Création d'un profil
	@PostMapping("/create")
	public String create(@PathVariable UUID userId, @ModelAttribute ProfileRequestDto profileDto, ModelMap model) {
		try {
			profileDto.setOwnerId(userId);
			profileService.createProfile(profileDto);
			return "redirect:/users/" + userId + "/profiles";
		} catch (IllegalArgumentException e) {
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

	// Voir un profil spécifique
	@GetMapping("/{profileId}")
	public String show(@PathVariable UUID userId, @PathVariable UUID profileId, ModelMap model) {
		Profile profile = profileService.getProfileById(profileId);
		User user = userService.getUserById(userId);
		model.addAttribute("profile", profile);
		model.addAttribute("user", user);
		model.addAttribute("userId", userId);
		return "/profiles/show";
	}

	// Supprimer un profil
	@PostMapping("/{profileId}/delete")
	public RedirectView delete(@PathVariable UUID userId, @PathVariable UUID profileId) {
		profileService.archiveProfile(profileId);
		return new RedirectView("/users/" + userId + "/profiles");
	}
}
