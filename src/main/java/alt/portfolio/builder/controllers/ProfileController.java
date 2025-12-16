package alt.portfolio.builder.controllers;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
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

import alt.portfolio.builder.dtos.ProfileRequestDto;
import alt.portfolio.builder.entities.Profile;
import alt.portfolio.builder.services.DbProfileService;
import alt.portfolio.builder.services.ProfileService;

@RequestMapping("/profiles")
@Controller
public class ProfileController {

	@Autowired
	private ProfileService profileService;

	@Autowired
	private DbProfileService dbProfileServices;

	@GetMapping(path = { "", "/" })
	public ModelAndView index() {
		return new ModelAndView("/profiles/index", "profiles", profileService.getProfiles());
	}

	@GetMapping("/create")
	public String create(ModelMap model) {
		model.addAttribute("profile", new Profile());
		return "/profiles/profileform";
	}

	@PostMapping("/create")
	public String createProfile(@ModelAttribute ProfileRequestDto createdProfile, BindingResult bindingResult,
			ModelMap model) {
		try {
			profileService.createProfile(createdProfile);
			return "redirect:/profiles";
		} catch (IllegalArgumentException e) {
			// on ne réutilise pas le message exact, on met un message générique
			model.addAttribute("profile", createdProfile);
			return "/profiles/profileform";
		}
	}

	@GetMapping("/{ownerId}/{id}")
	public String show(@PathVariable UUID id, ModelMap model) {
		Profile profile = profileService.getProfileById(id);
		model.addAttribute("profile", profile);
		return "/profiles/show";
	}

	@PostMapping("/{id}/delete")
	public RedirectView delete(@PathVariable UUID id) {
		profileService.archiveProfile(id);
		return new RedirectView("/profiles");
	}

	@GetMapping("/register/{name}")
	@ResponseBody
	public Profile createProfile(@PathVariable String name) {
		return dbProfileServices.createProfile(name);
	}
}
