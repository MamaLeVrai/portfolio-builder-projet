package alt.portfolio.builder.controllers;

import java.util.UUID;

import alt.portfolio.builder.exceptions.EntityNotFoundException;
import alt.portfolio.builder.exceptions.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import alt.portfolio.builder.entities.User;
import alt.portfolio.builder.services.UserService;
import alt.portfolio.builder.utils.AuthUtils;

/**
 * Contrôleur pour la gestion des utilisateurs (admin uniquement)
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	private UserService userService;

	@Autowired
	private alt.portfolio.builder.services.ProfileService profileService;

	/**
	 * Liste tous les utilisateurs
	 */
	@GetMapping("/users")
	public String listUsers(ModelMap model) {
		if (!AuthUtils.isAdmin()) {
			return "redirect:/";
		}

		model.addAttribute("users", userService.getAllUsers());
		return "/admin/users/list";
	}

	/**
	 * Affiche les détails d'un utilisateur
	 */
	@GetMapping("/users/{id}")
	public String viewUser(@PathVariable UUID id, ModelMap model) throws EntityNotFoundException {
		if (!AuthUtils.isAdmin()) {
			return "redirect:/";
		}

		model.addAttribute("user", userService.getUserById(id));
		return "/admin/users/view";
	}

	/**
	 * Affiche le formulaire d'édition d'un utilisateur
	 */
	@GetMapping("/users/{id}/edit")
	public String editUser(@PathVariable UUID id, ModelMap model) throws EntityNotFoundException {
		if (!AuthUtils.isAdmin()) {
			return "redirect:/";
		}

		User user = userService.getUserById(id);
		model.addAttribute("user", user);
		model.addAttribute("isRoleUser", "USER".equals(user.getRole()));
		model.addAttribute("isRoleAdmin", "ADMIN".equals(user.getRole()));
		return "/admin/users/edit";
	}

	/**
	 * Met à jour un utilisateur
	 */
	@PostMapping("/users/{id}/edit")
	public String updateUser(@PathVariable UUID id, User userUpdate,
			RedirectAttributes redirectAttributes) throws EntityNotFoundException, ValidationException {
		if (!AuthUtils.isAdmin()) {
			return "redirect:/";
		}

		User existingUser = userService.getUserById(id);
		existingUser.setFirstname(userUpdate.getFirstname());
		existingUser.setLastname(userUpdate.getLastname());
		existingUser.setEmail(userUpdate.getEmail());
		existingUser.setRole(userUpdate.getRole());
		existingUser.setArchiver(userUpdate.isArchiver());

		userService.updateUser(existingUser);
		redirectAttributes.addFlashAttribute("successMessage", "Utilisateur mis à jour avec succès !");
		return "redirect:/admin/users";
	}

	/**
	 * Supprime un utilisateur
	 */
	@PostMapping("/users/{id}/delete")
	public String deleteUser(@PathVariable UUID id, RedirectAttributes redirectAttributes) throws EntityNotFoundException {
		if (!AuthUtils.isAdmin()) {
			return "redirect:/";
		}

		User currentUser = AuthUtils.getCurrentUser();
		if (currentUser != null && currentUser.getId().equals(id)) {
			redirectAttributes.addFlashAttribute("error", "Vous ne pouvez pas supprimer votre propre compte !");
			return "redirect:/admin/users";
		}

		userService.deleteUser(id);
		redirectAttributes.addFlashAttribute("successMessage", "Utilisateur supprimé avec succès !");
		return "redirect:/admin/users";
	}

	/**
	 * Affiche les profils d'un utilisateur
	 */
	@GetMapping("/users/{id}/profiles")
	public String viewUserProfiles(@PathVariable UUID id, ModelMap model) throws EntityNotFoundException {
		if (!AuthUtils.isAdmin()) {
			return "redirect:/";
		}

		User user = userService.getUserById(id);
		model.addAttribute("user", user);
		model.addAttribute("profiles", profileService.getProfilesByUserSorted(user));
		return "/admin/users/profiles";
	}

	/**
	 * Archive/Désarchive un utilisateur
	 */
	@PostMapping("/users/{id}/toggle-archive")
	public String toggleArchiveUser(@PathVariable UUID id, RedirectAttributes redirectAttributes) throws EntityNotFoundException, ValidationException {
		if (!AuthUtils.isAdmin()) {
			return "redirect:/";
		}

		User user = userService.getUserById(id);
		user.setArchiver(!user.isArchiver());
		userService.updateUser(user);

		String message = user.isArchiver() ? "Utilisateur archivé" : "Utilisateur désarchivé";
		redirectAttributes.addFlashAttribute("successMessage", message + " avec succès !");
		return "redirect:/admin/users";
	}
}
