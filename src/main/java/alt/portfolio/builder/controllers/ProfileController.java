package alt.portfolio.builder.controllers;

import java.util.List;
import java.util.UUID;

import alt.portfolio.builder.exceptions.EntityNotFoundException;
import alt.portfolio.builder.exceptions.UnauthorizedException;
import alt.portfolio.builder.exceptions.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import alt.portfolio.builder.dtos.ProfileCreateDto;
import alt.portfolio.builder.dtos.ProfileUpdateDto;
import alt.portfolio.builder.entities.Profile;
import alt.portfolio.builder.entities.User;
import alt.portfolio.builder.services.ProfileService;
import alt.portfolio.builder.utils.AuthUtils;
import jakarta.validation.Valid;

/**
 * Contrôleur pour la gestion des profils
 */
@RequestMapping("/profiles")
@Controller
public class ProfileController {

	@Autowired
	private ProfileService profileService;

	/**
	 * US-006: Affiche le formulaire de création de profil
	 */
	@GetMapping("/new")
	public String newProfile(ModelMap model) {
		if (!AuthUtils.isAuthenticated()) {
			return "redirect:/login";
		}
		model.addAttribute("profileCreate", new ProfileCreateDto());
		return "/profiles/create";
	}

	/**
	 * US-006: Crée un nouveau profil
	 */
	@PostMapping("/new")
	public String createNewProfile(@Valid @ModelAttribute("profileCreate") ProfileCreateDto createDto,
			ModelMap model, RedirectAttributes redirectAttributes) {

		User currentUser = AuthUtils.getCurrentUser();
		if (currentUser == null) {
			return "redirect:/login";
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

	/**
	 * US-007: Liste les profils de l'utilisateur connecté
	 */
	@GetMapping("/my-profiles")
	public String myProfiles(ModelMap model) {
		User currentUser = AuthUtils.getCurrentUser();
		if (currentUser == null) {
			return "redirect:/login";
		}

		List<Profile> profiles = profileService.getProfilesByUserSorted(currentUser);
		model.addAttribute("profiles", profiles);
		model.addAttribute("hasProfiles", !profiles.isEmpty());
		model.addAttribute("user", currentUser);
		return "/profiles/my-profiles";
	}

	/**
	 * Affiche les détails d'un profil
	 */
	@GetMapping("/{id}")
	public String show(@PathVariable UUID id, ModelMap model) {
		Profile profile = profileService.getProfileById(id);
		model.addAttribute("profile", profile);
		return "/profiles/show";
	}

	/**
	 * US-008: Affiche le formulaire d'édition d'un profil
	 */
	@GetMapping("/{id}/edit")
	public String editProfile(@PathVariable UUID id, ModelMap model) {
		User currentUser = AuthUtils.getCurrentUser();
		if (currentUser == null) {
			return "redirect:/login";
		}

		Profile profile = profileService.getProfileById(id);

		// Vérification de propriété
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

	/**
	 * US-008: Met à jour un profil
	 */
	@PostMapping("/{id}/edit")
	public String updateProfile(@PathVariable UUID id,
			@Valid @ModelAttribute("profileUpdate") ProfileUpdateDto updateDto,
			ModelMap model, RedirectAttributes redirectAttributes) {

		User currentUser = AuthUtils.getCurrentUser();
		if (currentUser == null) {
			return "redirect:/login";
		}

		Profile profile = profileService.getProfileById(id);

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

	/**
	 * US-009: Duplique un profil
	 */
	@PostMapping("/{id}/duplicate")
	public String duplicateProfile(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
		User currentUser = AuthUtils.getCurrentUser();
		if (currentUser == null) {
			return "redirect:/login";
		}

		try {
			profileService.duplicateProfile(id, currentUser);
			redirectAttributes.addFlashAttribute("successMessage", "Profil dupliqué avec succès !");
		} catch (IllegalArgumentException e) {
			redirectAttributes.addFlashAttribute("error", e.getMessage());
		}

		return "redirect:/profiles/my-profiles";
	}

	/**
	 * US-010: Supprime un profil
	 */
	@PostMapping("/{id}/delete-confirmed")
	public String deleteProfileConfirmed(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
		User currentUser = AuthUtils.getCurrentUser();
		if (currentUser == null) {
			return "redirect:/login";
		}

		try {
			profileService.deleteProfile(id, currentUser);
			redirectAttributes.addFlashAttribute("successMessage", "Profil supprimé avec succès !");
		} catch (IllegalArgumentException e) {
			redirectAttributes.addFlashAttribute("error", e.getMessage());
		}

		return "redirect:/profiles/my-profiles";
	}

	/**
	 * US-011: Définit un profil comme profil par défaut
	 */
	@PostMapping("/{id}/set-default")
	public String setDefaultProfile(@PathVariable UUID id, RedirectAttributes redirectAttributes) throws EntityNotFoundException, UnauthorizedException {
		User currentUser = AuthUtils.getCurrentUser();
		if (currentUser == null) {
			return "redirect:/login";
		}

		profileService.setDefaultProfile(id, currentUser);
		redirectAttributes.addFlashAttribute("successMessage", "Profil défini comme profil par défaut !");

		return "redirect:/profiles/my-profiles";
	}
}