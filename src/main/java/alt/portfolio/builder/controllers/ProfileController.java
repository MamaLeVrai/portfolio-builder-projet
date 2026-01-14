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

import alt.portfolio.builder.dtos.ProfileCreateDto;
import alt.portfolio.builder.dtos.ProfileRequestDto;
import alt.portfolio.builder.dtos.ProfileUpdateDto;
import alt.portfolio.builder.entities.Profile;
import alt.portfolio.builder.entities.User;
import alt.portfolio.builder.services.ProfileService;
import jakarta.validation.Valid;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

	// US-006: Create profile (new route)
	@GetMapping("/new")
	public String newProfile(ModelMap model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || !(auth.getPrincipal() instanceof User)) {
			return "redirect:/login";
		}

		model.addAttribute("profileCreate", new ProfileCreateDto());
		return "/profiles/create";
	}

	@PostMapping("/new")
	public String createNewProfile(@Valid @ModelAttribute("profileCreate") ProfileCreateDto createDto,
			BindingResult bindingResult, ModelMap model, RedirectAttributes redirectAttributes) {
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || !(auth.getPrincipal() instanceof User)) {
			return "redirect:/login";
		}

		User currentUser = (User) auth.getPrincipal();

		if (bindingResult.hasErrors()) {
			return "/profiles/create";
		}

		try {
			profileService.createProfileNew(createDto, currentUser);
			redirectAttributes.addFlashAttribute("successMessage", "Profil créé avec succès !");
			return "redirect:/profiles/my-profiles";
		} catch (IllegalArgumentException e) {
			model.addAttribute("error", e.getMessage());
			return "/profiles/create";
		}
	}

	// US-007: List my profiles
	@GetMapping("/my-profiles")
	public String myProfiles(ModelMap model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || !(auth.getPrincipal() instanceof User)) {
			return "redirect:/login";
		}

		User currentUser = (User) auth.getPrincipal();
		model.addAttribute("profiles", profileService.getProfilesByUserSorted(currentUser));
		model.addAttribute("user", currentUser);
		return "/profiles/my-profiles";
	}

	// US-008: Edit profile
	@GetMapping("/{id}/edit")
	public String editProfile(@PathVariable UUID id, ModelMap model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || !(auth.getPrincipal() instanceof User)) {
			return "redirect:/login";
		}

		User currentUser = (User) auth.getPrincipal();
		Profile profile = profileService.getProfileById(id);

		// Check ownership
		if (!profile.getOwner().getId().equals(currentUser.getId())) {
			return "redirect:/profiles/my-profiles";
		}

		ProfileUpdateDto updateDto = new ProfileUpdateDto();
		updateDto.setName(profile.getName());
		updateDto.setDescription(profile.getDescription());
		updateDto.setImageUrl(profile.getImageUrl());
		updateDto.setStatus(profile.getStatus());

		model.addAttribute("profileUpdate", updateDto);
		model.addAttribute("profile", profile);
		model.addAttribute("statusIsDraft", "draft".equals(profile.getStatus()));
		model.addAttribute("statusIsPublished", "published".equals(profile.getStatus()));
		model.addAttribute("statusIsArchived", "archived".equals(profile.getStatus()));
		return "/profiles/edit";
	}

	@PostMapping("/{id}/edit")
	public String updateProfile(@PathVariable UUID id,
			@Valid @ModelAttribute("profileUpdate") ProfileUpdateDto updateDto,
			BindingResult bindingResult, ModelMap model, RedirectAttributes redirectAttributes) {
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || !(auth.getPrincipal() instanceof User)) {
			return "redirect:/login";
		}

		User currentUser = (User) auth.getPrincipal();
		Profile profile = profileService.getProfileById(id);

		if (bindingResult.hasErrors()) {
			model.addAttribute("profile", profile);
			model.addAttribute("statusIsDraft", "draft".equals(updateDto.getStatus()));
			model.addAttribute("statusIsPublished", "published".equals(updateDto.getStatus()));
			model.addAttribute("statusIsArchived", "archived".equals(updateDto.getStatus()));
			return "/profiles/edit";
		}

		try {
			profileService.updateProfile(id, updateDto, currentUser);
			redirectAttributes.addFlashAttribute("successMessage", "Profil mis à jour avec succès !");
			return "redirect:/profiles/my-profiles";
		} catch (IllegalArgumentException e) {
			model.addAttribute("error", e.getMessage());
			model.addAttribute("profile", profile);
			model.addAttribute("statusIsDraft", "draft".equals(updateDto.getStatus()));
			model.addAttribute("statusIsPublished", "published".equals(updateDto.getStatus()));
			model.addAttribute("statusIsArchived", "archived".equals(updateDto.getStatus()));
			return "/profiles/edit";
		}
	}

	// US-009: Duplicate profile
	@PostMapping("/{id}/duplicate")
	public String duplicateProfile(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || !(auth.getPrincipal() instanceof User)) {
			return "redirect:/login";
		}

		User currentUser = (User) auth.getPrincipal();

		try {
			profileService.duplicateProfile(id, currentUser);
			redirectAttributes.addFlashAttribute("successMessage", "Profil dupliqué avec succès !");
		} catch (IllegalArgumentException e) {
			redirectAttributes.addFlashAttribute("error", e.getMessage());
		}

		return "redirect:/profiles/my-profiles";
	}

	// US-010: Delete profile (new route with ownership check)
	@PostMapping("/{id}/delete-confirmed")
	public String deleteProfileConfirmed(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || !(auth.getPrincipal() instanceof User)) {
			return "redirect:/login";
		}

		User currentUser = (User) auth.getPrincipal();

		try {
			profileService.deleteProfile(id, currentUser);
			redirectAttributes.addFlashAttribute("successMessage", "Profil supprimé avec succès !");
		} catch (IllegalArgumentException e) {
			redirectAttributes.addFlashAttribute("error", e.getMessage());
		}

		return "redirect:/profiles/my-profiles";
	}

	// US-011: Set default profile
	@PostMapping("/{id}/set-default")
	public String setDefaultProfile(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || !(auth.getPrincipal() instanceof User)) {
			return "redirect:/login";
		}

		User currentUser = (User) auth.getPrincipal();

		try {
			profileService.setDefaultProfile(id, currentUser);
			redirectAttributes.addFlashAttribute("successMessage", "Profil défini comme profil par défaut !");
		} catch (IllegalArgumentException e) {
			redirectAttributes.addFlashAttribute("error", e.getMessage());
		}

		return "redirect:/profiles/my-profiles";
	}
}