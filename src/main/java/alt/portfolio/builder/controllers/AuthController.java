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

@Controller
public class AuthController {

	@Autowired
	private UserService userService;

	@GetMapping("/register")
	public String showRegisterForm(ModelMap model) {
		model.addAttribute("userRegister", new UserRegisterDto());
		return "/users/register";
	}

	@PostMapping("/register")
	public String register(@Valid @ModelAttribute("userRegister") UserRegisterDto userRegisterDto,
			BindingResult bindingResult, ModelMap model, RedirectAttributes redirectAttributes) throws ValidationException {

		// Check validation errors
		if (bindingResult.hasErrors()) {
			return "/users/register";
		}

		// Check password match
		if (!userRegisterDto.getPassword().equals(userRegisterDto.getConfirmPassword())) {
			model.addAttribute("passwordError", "Les mots de passe ne correspondent pas");
			return "/users/register";
		}

		userService.registerUser(userRegisterDto);
		redirectAttributes.addFlashAttribute("successMessage",
				"Inscription réussie ! Vous pouvez maintenant vous connecter.");
		return "redirect:/login";
	}

	@GetMapping("/login")
	public String showLoginForm(ModelMap model) {
		// Spring Security handles the actual login
		return "/users/formLogin";
	}

	@GetMapping("/logout")
	public String logout(HttpServletRequest request, HttpServletResponse response,
			RedirectAttributes redirectAttributes) {
		// Invalidate session
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null) {
			SecurityContextHolder.clearContext();
			request.getSession().invalidate();
		}
		redirectAttributes.addFlashAttribute("successMessage", "Vous avez été déconnecté avec succès.");
		return "redirect:/login";
	}
}
