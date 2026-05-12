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
 * Contrôleur qui gère les actions liées au compte de l'utilisateur connecté :
 * modifier ses informations personnelles, supprimer son compte, voir son profil.
 *
 * Toutes les routes commencent par "/users/".
 */
@RequestMapping("/users")
@Controller
public class UserController {

	@Autowired
	private UserService userService;

	/**
	 * Quand on visite /users ou /users/, on est directement redirigé
	 * vers la liste des profils. C'est la page principale après la connexion.
	 */
	@GetMapping(path = { "", "/" })
	public String index() {
		return "redirect:/profiles/my-profiles";
	}

	/**
	 * US-004 : Affiche le formulaire de modification du compte.
	 * URL : GET /users/edit
	 *
	 * On pré-remplit le formulaire avec les infos actuelles de l'utilisateur
	 * (prénom, nom, email) pour qu'il n'ait qu'à modifier ce qu'il veut changer.
	 */
	@GetMapping("/edit")
	public String editProfile(ModelMap model) {
		User currentUser = AuthUtils.getCurrentUser();
		if (currentUser == null) {
			return "redirect:/login";
		}

		// On crée un DTO pré-rempli avec les valeurs actuelles
		UserUpdateDto updateDto = new UserUpdateDto();
		updateDto.setFirstname(currentUser.getFirstname());
		updateDto.setLastname(currentUser.getLastname());
		updateDto.setEmail(currentUser.getEmail());

		model.addAttribute("userUpdate", updateDto);
		model.addAttribute("user", currentUser);
		return "/users/edit";
	}

	/**
	 * US-004 : Enregistre les modifications du compte utilisateur.
	 * URL : POST /users/edit
	 *
	 * On vérifie d'abord que les nouvelles valeurs sont valides,
	 * puis on met à jour la base de données.
	 */
	@PostMapping("/edit")
	public String updateProfile(@Valid @ModelAttribute("userUpdate") UserUpdateDto updateDto,
			BindingResult bindingResult, ModelMap model, RedirectAttributes redirectAttributes) {

		User currentUser = AuthUtils.getCurrentUser();
		if (currentUser == null) {
			return "redirect:/login";
		}

		// Si le formulaire a des erreurs de validation, on réaffiche le formulaire avec les erreurs
		if (bindingResult.hasErrors()) {
			model.addAttribute("user", currentUser);
			return "/users/edit";
		}

		try {
			userService.updateUser(currentUser.getId(), updateDto);
			redirectAttributes.addFlashAttribute("successMessage", "Profil mis à jour avec succès !");
			return "redirect:/users/edit";
		} catch (IllegalArgumentException e) {
			// Ex : email déjà utilisé par quelqu'un d'autre
			model.addAttribute("error", e.getMessage());
			model.addAttribute("user", currentUser);
			return "/users/edit";
		}
	}

	/**
	 * US-005 : Supprime définitivement le compte de l'utilisateur connecté.
	 * URL : POST /users/delete-account
	 *
	 * Après la suppression, on déconnecte l'utilisateur et on le redirige
	 * vers la page d'inscription (puisque son compte n'existe plus).
	 */
	@PostMapping("/delete-account")
	public String deleteAccount(HttpServletRequest request, RedirectAttributes redirectAttributes) throws EntityNotFoundException {
		User currentUser = AuthUtils.getCurrentUser();
		if (currentUser == null) {
			return "redirect:/login";
		}

		UUID userId = currentUser.getId();
		userService.deleteAccount(userId);

		// On efface la session pour déconnecter l'utilisateur après suppression
		org.springframework.security.core.context.SecurityContextHolder.clearContext();
		request.getSession().invalidate();

		redirectAttributes.addFlashAttribute("successMessage", "Votre compte a été supprimé avec succès.");
		return "redirect:/register";
	}

	/**
	 * Affiche le détail d'un compte utilisateur.
	 * URL : GET /users/{id}
	 * Seul le propriétaire du compte ou un admin peut voir cette page.
	 */
	@GetMapping("/{id}")
	public String show(@PathVariable UUID id, ModelMap model) throws EntityNotFoundException {
		// Vérification : est-ce que c'est mon compte ou suis-je admin ?
		if (!AuthUtils.isOwnerOrAdmin(id)) {
			return "redirect:/";
		}

		User user = userService.getUserById(id);
		model.addAttribute("user", user);
		return "/users/show";
	}
}