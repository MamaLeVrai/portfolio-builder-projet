package alt.portfolio.builder.controllers;

import alt.portfolio.builder.exceptions.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import alt.portfolio.builder.dtos.UserRegisterDto;
import alt.portfolio.builder.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

/**
 * Contrôleur qui gère l'inscription, la connexion et la déconnexion des utilisateurs.
 *
 * Ce sont les trois actions les plus basiques de l'application :
 * - S'inscrire pour créer un compte
 * - Se connecter pour accéder à son espace
 * - Se déconnecter quand on a fini
 */
@Controller
public class AuthController {

	@Autowired
	private UserService userService;

	/**
	 * Affiche la page d'inscription avec un formulaire vide.
	 * URL : GET /register
	 * On crée un objet UserRegisterDto vide et on l'envoie au template
	 * pour que le formulaire puisse le remplir.
	 */
	@GetMapping("/register")
	public String showRegisterForm(ModelMap model) {
		model.addAttribute("userRegister", new UserRegisterDto());
		return "/users/register";
	}

	/**
	 * Traite le formulaire d'inscription soumis par l'utilisateur.
	 * URL : POST /register
	 *
	 * @Valid vérifie automatiquement les contraintes définies dans UserRegisterDto
	 * (champs obligatoires, taille max, format email, etc.)
	 * BindingResult contient les éventuelles erreurs de validation.
	 */
	@PostMapping("/register")
	public String register(@Valid @ModelAttribute("userRegister") UserRegisterDto userRegisterDto,
			BindingResult bindingResult, ModelMap model, RedirectAttributes redirectAttributes) throws ValidationException {

		// Si les champs du formulaire ont des erreurs (ex : email invalide), on réaffiche le formulaire
		if (bindingResult.hasErrors()) {
			return "/users/register";
		}

		// Vérification supplémentaire : les deux mots de passe doivent être identiques
		if (!userRegisterDto.getPassword().equals(userRegisterDto.getConfirmPassword())) {
			model.addAttribute("passwordError", "Les mots de passe ne correspondent pas");
			return "/users/register";
		}

		// Tout est bon : on crée le compte et on redirige vers la connexion
		userService.registerUser(userRegisterDto);
		redirectAttributes.addFlashAttribute("successMessage",
				"Inscription réussie ! Vous pouvez maintenant vous connecter.");
		return "redirect:/login";
	}

	/**
	 * Affiche la page de connexion.
	 * URL : GET /login
	 * Spring Security gère lui-même la vérification du mot de passe.
	 * Nous, on affiche juste la page avec le formulaire.
	 */
	@GetMapping("/login")
	public String showLoginForm(ModelMap model) {
		return "/users/formLogin";
	}

	/**
	 * Déconnecte l'utilisateur et le redirige vers la page de connexion.
	 * URL : GET /logout
	 * On efface les informations de session pour que l'utilisateur soit
	 * complètement déconnecté.
	 */
	@GetMapping("/logout")
	public String logout(HttpServletRequest request, HttpServletResponse response,
			RedirectAttributes redirectAttributes) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null) {
			// Efface les informations de connexion de Spring Security
			SecurityContextHolder.clearContext();
			// Détruit la session HTTP du navigateur
			request.getSession().invalidate();
		}
		redirectAttributes.addFlashAttribute("successMessage", "Vous avez été déconnecté avec succès.");
		return "redirect:/login";
	}
}
