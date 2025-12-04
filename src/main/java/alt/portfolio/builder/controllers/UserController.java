package alt.portfolio.builder.controllers;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import alt.portfolio.builder.dtos.UserDeleteDto;
import alt.portfolio.builder.dtos.userRequestDto;
import alt.portfolio.builder.entities.User;
import alt.portfolio.builder.services.UserService;

@RequestMapping("users")
@Controller
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@GetMapping(path = {"","/"})
	public ModelAndView index() {
		return new ModelAndView("/users/index","users",userService.getUsers());
	}
	
	@GetMapping("/create")
	public String create(ModelMap model) {
		model.addAttribute("user", new User());
		return "/users/userForm";
	}
	
	@PostMapping("/create")
	public RedirectView createUser(@ModelAttribute userRequestDto createdUser) {
		User user = userService.createUser(createdUser);
		return new RedirectView("/users");
	}
	
    @GetMapping("/{id}")
    public String show(@PathVariable UUID id, ModelMap model) {
        User user = userService.getUserById(id);
        model.addAttribute("user", user);
        return "/users/show";
    }
	
    @PostMapping("/{id}/delete")
    public RedirectView delete(@PathVariable UUID id) {
        userService.deleteUser(id);
        return new RedirectView("/users");
    }
}
