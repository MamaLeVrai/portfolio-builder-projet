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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import alt.portfolio.builder.dtos.UserRequestDto;
import alt.portfolio.builder.entities.User;
import alt.portfolio.builder.services.DbUserServices;
import alt.portfolio.builder.services.UserService;

@RequestMapping("/users")
@Controller
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private DbUserServices dbUserServices;

	@GetMapping(path = { "", "/" })
	public ModelAndView index() {
		return new ModelAndView("/users/index", "users", userService.getUsers());
	}

	@GetMapping("/create")
	public String create(ModelMap model) {
		model.addAttribute("user", new User());
		return "/users/userForm";
	}

	@PostMapping("/create")
	public String createUser(@ModelAttribute UserRequestDto createdUser, BindingResult bindingResult, ModelMap model) {
		try {
			userService.createUser(createdUser);
			return "redirect:/users";
		} catch (IllegalArgumentException e) {
			// on ne réutilise pas le message exact, on met un message générique
			model.addAttribute("user", createdUser);
			model.addAttribute("emailError", true);
			return "/users/userForm";
		}
	}

	@GetMapping("/{id}")
	public String show(@PathVariable UUID id, ModelMap model) {
		User user = userService.getUserById(id);
		model.addAttribute("user", user);
		return "/users/show";
	}

	@PostMapping("/{id}/delete")
	public RedirectView delete(@PathVariable UUID id) {
		// on archive au lieu de supprimer
		userService.archiveUser(id);
		return new RedirectView("/users");
	}

	@GetMapping("/register/{username}/{password}")
	@ResponseBody
	public User createUser(@PathVariable String username, @PathVariable String password) {
		return dbUserServices.createUser(username, password);
	}

}