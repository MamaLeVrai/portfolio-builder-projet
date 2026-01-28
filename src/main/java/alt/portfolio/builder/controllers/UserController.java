package alt.portfolio.builder.controllers;

import java.util.UUID;

import alt.portfolio.builder.exceptions.EntityNotFoundException;
import alt.portfolio.builder.exceptions.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import alt.portfolio.builder.dtos.UserUpdateDto;
import alt.portfolio.builder.entities.User;
import alt.portfolio.builder.services.UserService;
import alt.portfolio.builder.utils.AuthUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

/**
 * Contrôleur pour la gestion des comptes utilisateurs
 */
@RequestMapping("/users")
@Controller
public class UserController {

	@Autowired
	private UserService userService;

	/**
	 * Page d'accueil - redirige vers mes profils
	 */
	@GetMapping(path = { "", "/" })
	public String index() {
		return "redirect:/profiles/my-profiles";
	}

	/**
	 * US-004: Affiche le formulaire d'édition du profil utilisateur
	 */
	@GetMapping("/edit")
	public String editProfile(ModelMap model) {
		User currentUser = AuthUtils.getCurrentUser();
		if (currentUser == null) {
			return "redirect:/login";
		}

		UserUpdateDto updateDto = new UserUpdateDto();
		updateDto.setFirstname(currentUser.getFirstname());
		updateDto.setLastname(currentUser.getLastname());
		updateDto.setEmail(currentUser.getEmail());

		model.addAttribute("userUpdate", updateDto);
		model.addAttribute("user", currentUser);
		return "/users/edit";
	}

	/**
	 * US-004: Met à jour le profil utilisateur
	 */
	@PostMapping("/edit")
	public String updateProfile(@Valid @ModelAttribute("userUpdate") UserUpdateDto updateDto,
			BindingResult bindingResult, ModelMap model, RedirectAttributes redirectAttributes) {
		
		User currentUser = AuthUtils.getCurrentUser();
		if (currentUser == null) {
			return "redirect:/login";
		}

		if (bindingResult.hasErrors()) {
			model.addAttribute("user", currentUser);
			return "/users/edit";
		}

		try {
			userService.updateUser(currentUser.getId(), updateDto);
			redirectAttributes.addFlashAttribute("successMessage", "Profil mis à jour avec succès !");
			return "redirect:/users/edit";
		} catch (IllegalArgumentException e) {
			model.addAttribute("error", e.getMessage());
			model.addAttribute("user", currentUser);
			return "/users/edit";
		}
	}

	/**
	 * US-005: Supprime le compte utilisateur
	 */
	@PostMapping("/delete-account")
	public String deleteAccount(HttpServletRequest request, RedirectAttributes redirectAttributes) throws EntityNotFoundException {
		User currentUser = AuthUtils.getCurrentUser();
		if (currentUser == null) {
			return "redirect:/login";
		}

		UUID userId = currentUser.getId();
		userService.deleteAccount(userId);

		// Invalider la session
		org.springframework.security.core.context.SecurityContextHolder.clearContext();
		request.getSession().invalidate();

		redirectAttributes.addFlashAttribute("successMessage", "Votre compte a été supprimé avec succès.");
		return "redirect:/register";
	}

	/**
	 * Affiche les détails d'un utilisateur (admin ou propriétaire)
	 */
	@GetMapping("/{id}")
	public String show(@PathVariable UUID id, ModelMap model) throws EntityNotFoundException {
		if (!AuthUtils.isOwnerOrAdmin(id)) {
			return "redirect:/";
		}

		User user = userService.getUserById(id);
		model.addAttribute("user", user);
		return "/users/show";
	}
}