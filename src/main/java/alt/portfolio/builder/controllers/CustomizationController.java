package alt.portfolio.builder.controllers;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import alt.portfolio.builder.entities.Profile;
import alt.portfolio.builder.entities.Template;
import alt.portfolio.builder.entities.User;
import alt.portfolio.builder.exceptions.UnauthorizedException;
import alt.portfolio.builder.services.ImageUploadService;
import alt.portfolio.builder.services.ItemService;
import alt.portfolio.builder.services.ProfileService;
import alt.portfolio.builder.utils.AuthUtils;

@RequestMapping("/profiles/{id}/customize")
@Controller
public class CustomizationController {

	@Autowired
	private ProfileService profileService;

	@Autowired
	private ImageUploadService imageUploadService;

	@Autowired
	private ItemService itemService;

	// US-024 / US-025 / US-028 / US-029 : Page de personnalisation
	@GetMapping
	public String showCustomize(@PathVariable UUID id, ModelMap model) {
		User currentUser = AuthUtils.getCurrentUser();
		if (currentUser == null) return "redirect:/login";

		Profile profile = profileService.getProfileById(id);
		if (!profile.getOwner().getId().equals(currentUser.getId())) {
			return "redirect:/profiles/my-profiles";
		}

		List<Template> templates = profileService.getAllTemplates();

		// Palettes prédéfinies
		List<String> palette = List.of(
			"#2c3e50", "#2980b9", "#27ae60", "#8e44ad",
			"#c0392b", "#d35400", "#16a085", "#f39c12",
			"#667eea", "#764ba2", "#1a1a2e", "#e74c3c"
		);

		model.addAttribute("profile", profile);
		model.addAttribute("templates", templates);
		model.addAttribute("palette", palette);
		model.addAttribute("currentCvTemplateId", profile.getTemplate() != null ? profile.getTemplate().getId() : null);
		model.addAttribute("currentPortfolioTemplateId", profile.getTemplate1() != null ? profile.getTemplate1().getId() : null);
		return "/profiles/customize";
	}

	// US-025 / US-028 : Sauvegarder couleur
	@PostMapping("/color")
	public String saveColor(@PathVariable UUID id,
			@RequestParam String color,
			@RequestParam String viewType,
			RedirectAttributes redirectAttributes) throws UnauthorizedException {
		User currentUser = AuthUtils.getCurrentUser();
		if (currentUser == null) return "redirect:/login";

		profileService.setColor(id, color, viewType, currentUser);
		redirectAttributes.addFlashAttribute("successMessage", "Couleur enregistrée !");
		return "redirect:/profiles/" + id + "/customize";
	}

	// US-024 : Sauvegarder template
	@PostMapping("/template")
	public String saveTemplate(@PathVariable UUID id,
			@RequestParam UUID templateId,
			@RequestParam String viewType,
			RedirectAttributes redirectAttributes) throws UnauthorizedException {
		User currentUser = AuthUtils.getCurrentUser();
		if (currentUser == null) return "redirect:/login";

		profileService.setTemplate(id, templateId, viewType, currentUser);
		redirectAttributes.addFlashAttribute("successMessage", "Template appliqué !");
		return "redirect:/profiles/" + id + "/customize";
	}

	// US-030 : Upload photo de profil
	@PostMapping("/upload-photo")
	public String uploadPhoto(@PathVariable UUID id,
			@RequestParam MultipartFile photo,
			RedirectAttributes redirectAttributes) throws UnauthorizedException {
		User currentUser = AuthUtils.getCurrentUser();
		if (currentUser == null) return "redirect:/login";

		try {
			String url = imageUploadService.uploadProfilePhoto(photo);
			profileService.setProfilePhoto(id, url, currentUser);
			redirectAttributes.addFlashAttribute("successMessage", "Photo mise à jour !");
		} catch (IOException | IllegalArgumentException e) {
			redirectAttributes.addFlashAttribute("error", e.getMessage());
		}
		return "redirect:/profiles/" + id + "/customize";
	}

	// US-031 : Upload image d'un item de projet
	@PostMapping("/items/{itemId}/upload-image")
	public String uploadItemImage(@PathVariable UUID id,
			@PathVariable UUID itemId,
			@RequestParam MultipartFile image,
			RedirectAttributes redirectAttributes) {
		User currentUser = AuthUtils.getCurrentUser();
		if (currentUser == null) return "redirect:/login";

		try {
			String url = imageUploadService.uploadItemImage(image);
			itemService.setItemImage(itemId, url);
			redirectAttributes.addFlashAttribute("successMessage", "Image ajoutée !");
		} catch (IOException | IllegalArgumentException e) {
			redirectAttributes.addFlashAttribute("error", e.getMessage());
		}
		return "redirect:/profiles/" + id + "/customize";
	}
}
