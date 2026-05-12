package alt.portfolio.builder.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import alt.portfolio.builder.entities.Profile;
import alt.portfolio.builder.exceptions.EntityNotFoundException;
import alt.portfolio.builder.services.ProfileService;
import jakarta.servlet.http.HttpServletRequest;

@RequestMapping("/public")
@Controller
public class PublicController {

	@Autowired
	private ProfileService profileService;

	@GetMapping("/cv/{username}")
	public String viewCv(@PathVariable String username, ModelMap model, HttpServletRequest request) throws EntityNotFoundException {
		Profile profile = profileService.getPublicCvProfile(username);

		profileService.recordView(profile, "cv", request.getRemoteAddr());

		model.addAttribute("profile", profile);
		model.addAttribute("owner", profile.getOwner());
		model.addAttribute("mode", "cv");
		model.addAttribute("isCvMode", true);
		model.addAttribute("isPortfolioMode", false);
		return "/public/cv";
	}

	@GetMapping("/portfolio/{username}")
	public String viewPortfolio(@PathVariable String username, ModelMap model, HttpServletRequest request) throws EntityNotFoundException {
		Profile profile = profileService.getPublicPortfolioProfile(username);

		profileService.recordView(profile, "portfolio", request.getRemoteAddr());

		model.addAttribute("profile", profile);
		model.addAttribute("owner", profile.getOwner());
		model.addAttribute("mode", "portfolio");
		model.addAttribute("isCvMode", false);
		model.addAttribute("isPortfolioMode", true);
		return "/public/portfolio";
	}
}
