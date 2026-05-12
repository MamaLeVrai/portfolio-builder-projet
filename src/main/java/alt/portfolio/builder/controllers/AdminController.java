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
 * Contrôleur réservé à l'administrateur pour gérer tous les utilisateurs.
 *
 * Toutes les routes de ce contrôleur commencent par "/admin/".
 * Chaque méthode vérifie d'abord que l'utilisateur est bien admin
 * avant de faire quoi que ce soit. Si ce n'est pas un admin, il est
 * redirigé vers la page d'accueil.
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	private UserService userService;

	@Autowired
	private alt.portfolio.builder.services.ProfileService profileService;

	/**
	 * Affiche la liste de TOUS les utilisateurs (même les archivés).
	 * URL : GET /admin/users
	 * Page HTML : /admin/users/list.html
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
	 * Affiche le détail d'un utilisateur précis.
	 * URL : GET /admin/users/{id}
	 * L'id dans l'URL est l'identifiant unique de l'utilisateur.
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
	 * Affiche le formulaire permettant à l'admin de modifier les infos d'un utilisateur.
	 * URL : GET /admin/users/{id}/edit
	 * isRoleUser et isRoleAdmin sont envoyés au template pour pré-sélectionner le bon rôle.
	 */
	@GetMapping("/users/{id}/edit")
	public String editUser(@PathVariable UUID id, ModelMap model) throws EntityNotFoundException {
		if (!AuthUtils.isAdmin()) {
			return "redirect:/";
		}

		User user = userService.getUserById(id);
		model.addAttribute("user", user);
		// Ces deux variables aident le template à cocher la bonne option de rôle
		model.addAttribute("isRoleUser", "USER".equals(user.getRole()));
		model.addAttribute("isRoleAdmin", "ADMIN".equals(user.getRole()));
		return "/admin/users/edit";
	}

	/**
	 * Enregistre les modifications faites sur un utilisateur par l'admin.
	 * URL : POST /admin/users/{id}/edit
	 * L'admin peut changer le prénom, nom, email, rôle et le statut archivé.
	 */
	@PostMapping("/users/{id}/edit")
	public String updateUser(@PathVariable UUID id, User userUpdate,
			RedirectAttributes redirectAttributes) throws EntityNotFoundException, ValidationException {
		if (!AuthUtils.isAdmin()) {
			return "redirect:/";
		}

		// On charge l'utilisateur existant depuis la base de données
		User existingUser = userService.getUserById(id);
		// On applique les nouvelles valeurs envoyées par le formulaire
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
	 * Supprime définitivement un utilisateur de la base de données.
	 * URL : POST /admin/users/{id}/delete
	 * L'admin ne peut pas se supprimer lui-même, on vérifie ça avant d'agir.
	 */
	@PostMapping("/users/{id}/delete")
	public String deleteUser(@PathVariable UUID id, RedirectAttributes redirectAttributes) throws EntityNotFoundException {
		if (!AuthUtils.isAdmin()) {
			return "redirect:/";
		}

		// Sécurité : un admin ne peut pas supprimer son propre compte depuis ici
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
	 * Affiche tous les profils appartenant à un utilisateur donné.
	 * URL : GET /admin/users/{id}/profiles
	 * Pratique pour qu'un admin puisse inspecter ce qu'a créé un utilisateur.
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
	 * Archive ou désarchive un utilisateur (c'est un "soft delete").
	 * URL : POST /admin/users/{id}/toggle-archive
	 * Archiver un utilisateur ne le supprime pas, il est juste désactivé.
	 * On peut l'activer à nouveau à tout moment en rappelant cette même URL.
	 */
	@PostMapping("/users/{id}/toggle-archive")
	public String toggleArchiveUser(@PathVariable UUID id, RedirectAttributes redirectAttributes) throws EntityNotFoundException, ValidationException {
		if (!AuthUtils.isAdmin()) {
			return "redirect:/";
		}

		User user = userService.getUserById(id);
		// On inverse l'état : archivé devient actif et vice versa
		user.setArchiver(!user.isArchiver());
		userService.updateUser(user);

		String message = user.isArchiver() ? "Utilisateur archivé" : "Utilisateur désarchivé";
		redirectAttributes.addFlashAttribute("successMessage", message + " avec succès !");
		return "redirect:/admin/users";
	}
}
