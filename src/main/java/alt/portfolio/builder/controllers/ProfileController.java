package alt.portfolio.builder.controllers;

import java.util.List;
import java.util.UUID;

import alt.portfolio.builder.exceptions.EntityNotFoundException;
import alt.portfolio.builder.exceptions.UnauthorizedException;
import alt.portfolio.builder.exceptions.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import alt.portfolio.builder.dtos.ProfileCreateDto;
import alt.portfolio.builder.dtos.ProfileUpdateDto;
import alt.portfolio.builder.entities.ContactMessage;
import alt.portfolio.builder.entities.Profile;
import alt.portfolio.builder.entities.ProfileView;
import alt.portfolio.builder.entities.User;
import alt.portfolio.builder.services.ProfileService;
import alt.portfolio.builder.utils.AuthUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

/**
 * Contrôleur pour la gestion des profils
 */
@RequestMapping("/profiles")
@Controller
public class ProfileController {

	@Autowired
	private ProfileService profileService;

	/**
	 * US-006: Affiche le formulaire de création de profil
	 */
	@GetMapping("/new")
	public String newProfile(ModelMap model) {
		if (!AuthUtils.isAuthenticated()) {
			return "redirect:/login";
		}
		model.addAttribute("profileCreate", new ProfileCreateDto());
		return "/profiles/create";
	}

	/**
	 * US-006: Crée un nouveau profil
	 */
	@PostMapping("/new")
	public String createNewProfile(@Valid @ModelAttribute("profileCreate") ProfileCreateDto createDto,
			ModelMap model, RedirectAttributes redirectAttributes) {

		User currentUser = AuthUtils.getCurrentUser();
		if (currentUser == null) {
			return "redirect:/login";
		}

		try {
			profileService.createProfileNew(createDto, currentUser);
			redirectAttributes.addFlashAttribute("successMessage", "Profil créé avec succès !");
			return "redirect:/profiles/my-profiles";
		} catch (IllegalArgumentException e) {
			model.addAttribute("error", e.getMessage());
			return "/profiles/create";
		}
	}

	/**
	 * US-007: Liste les profils de l'utilisateur connecté
	 */
	@GetMapping("/my-profiles")
	public String myProfiles(ModelMap model) {
		User currentUser = AuthUtils.getCurrentUser();
		if (currentUser == null) {
			return "redirect:/login";
		}

		List<Profile> profiles = profileService.getProfilesByUserSorted(currentUser);
		model.addAttribute("profiles", profiles);
		model.addAttribute("hasProfiles", !profiles.isEmpty());
		model.addAttribute("user", currentUser);
		return "/profiles/my-profiles";
	}

	/**
	 * Affiche les détails d'un profil
	 */
	@GetMapping("/{id}")
	public String show(@PathVariable UUID id, ModelMap model) {
		Profile profile = profileService.getProfileById(id);
		model.addAttribute("profile", profile);
		return "/profiles/show";
	}

	/**
	 * US-008: Affiche le formulaire d'édition d'un profil
	 */
	@GetMapping("/{id}/edit")
	public String editProfile(@PathVariable UUID id, ModelMap model) {
		User currentUser = AuthUtils.getCurrentUser();
		if (currentUser == null) {
			return "redirect:/login";
		}

		Profile profile = profileService.getProfileById(id);

		// Vérification de propriété
		if (!profile.getOwner().getId().equals(currentUser.getId())) {
			return "redirect:/profiles/my-profiles";
		}

		ProfileUpdateDto updateDto = new ProfileUpdateDto();
		updateDto.setName(profile.getName());
		updateDto.setDescription(profile.getDescription());
		updateDto.setImageUrl(profile.getImageUrl());
		updateDto.setStatus(profile.getStatus());

		model.addAttribute("profileUpdate", updateDto);
		model.addAttribute("profile", profile);
		model.addAttribute("statusIsDraft", "draft".equals(profile.getStatus()));
		model.addAttribute("statusIsPublished", "published".equals(profile.getStatus()));
		model.addAttribute("statusIsArchived", "archived".equals(profile.getStatus()));
		return "/profiles/edit";
	}

	/**
	 * US-008: Met à jour un profil
	 */
	@PostMapping("/{id}/edit")
	public String updateProfile(@PathVariable UUID id,
			@Valid @ModelAttribute("profileUpdate") ProfileUpdateDto updateDto,
			ModelMap model, RedirectAttributes redirectAttributes) {

		User currentUser = AuthUtils.getCurrentUser();
		if (currentUser == null) {
			return "redirect:/login";
		}

		Profile profile = profileService.getProfileById(id);

		try {
			profileService.updateProfile(id, updateDto, currentUser);
			redirectAttributes.addFlashAttribute("successMessage", "Profil mis à jour avec succès !");
			return "redirect:/profiles/my-profiles";
		} catch (IllegalArgumentException e) {
			model.addAttribute("error", e.getMessage());
			model.addAttribute("profile", profile);
			model.addAttribute("statusIsDraft", "draft".equals(updateDto.getStatus()));
			model.addAttribute("statusIsPublished", "published".equals(updateDto.getStatus()));
			model.addAttribute("statusIsArchived", "archived".equals(updateDto.getStatus()));
			return "/profiles/edit";
		}
	}

	/**
	 * US-009: Duplique un profil
	 */
	@PostMapping("/{id}/duplicate")
	public String duplicateProfile(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
		User currentUser = AuthUtils.getCurrentUser();
		if (currentUser == null) {
			return "redirect:/login";
		}

		try {
			profileService.duplicateProfile(id, currentUser);
			redirectAttributes.addFlashAttribute("successMessage", "Profil dupliqué avec succès !");
		} catch (IllegalArgumentException e) {
			redirectAttributes.addFlashAttribute("error", e.getMessage());
		}

		return "redirect:/profiles/my-profiles";
	}

	/**
	 * US-010: Supprime un profil
	 */
	@PostMapping("/{id}/delete-confirmed")
	public String deleteProfileConfirmed(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
		User currentUser = AuthUtils.getCurrentUser();
		if (currentUser == null) {
			return "redirect:/login";
		}

		try {
			profileService.deleteProfile(id, currentUser);
			redirectAttributes.addFlashAttribute("successMessage", "Profil supprimé avec succès !");
		} catch (IllegalArgumentException e) {
			redirectAttributes.addFlashAttribute("error", e.getMessage());
		}

		return "redirect:/profiles/my-profiles";
	}

	/**
	 * US-011: Définit un profil comme profil par défaut
	 */
	@PostMapping("/{id}/set-default")
	public String setDefaultProfile(@PathVariable UUID id, RedirectAttributes redirectAttributes) throws EntityNotFoundException, UnauthorizedException {
		User currentUser = AuthUtils.getCurrentUser();
		if (currentUser == null) {
			return "redirect:/login";
		}

		profileService.setDefaultProfile(id, currentUser);
		redirectAttributes.addFlashAttribute("successMessage", "Profil défini comme profil par défaut !");

		return "redirect:/profiles/my-profiles";
	}

	/**
	 * (Epic 4 - US-021 / US-026) Prévisualise un profil AVANT de le publier.
	 * URL : GET /profiles/{id}/preview?mode=cv  ou  ?mode=portfolio
	 *
	 * L'utilisateur peut basculer entre la vue CV et la vue Portfolio
	 * grâce au paramètre "mode". Seul le propriétaire peut accéder à cette page.
	 *
	 * @param mode "cv" (par défaut) ou "portfolio"
	 */
	@GetMapping("/{id}/preview")
	public String previewProfile(@PathVariable UUID id,
			@RequestParam(defaultValue = "cv") String mode,
			ModelMap model) {
		User currentUser = AuthUtils.getCurrentUser();
		if (currentUser == null) {
			return "redirect:/login";
		}

		Profile profile = profileService.getProfileById(id);
		if (!profile.getOwner().getId().equals(currentUser.getId())) {
			return "redirect:/profiles/my-profiles";
		}

		model.addAttribute("profile", profile);
		model.addAttribute("mode", mode);
		model.addAttribute("isCvMode", "cv".equals(mode));           // true si on est en mode CV
		model.addAttribute("isPortfolioMode", "portfolio".equals(mode)); // true si Portfolio
		model.addAttribute("owner", profile.getOwner());
		return "/profiles/preview";
	}

	/**
	 * (Epic 4 - US-022) Soumet le formulaire de publication.
	 * URL : POST /profiles/{id}/publish
	 *
	 * Les cases à cocher "publishCv" et "publishPortfolio" viennent du formulaire HTML.
	 * Si aucune case n'est cochée → dépublication totale.
	 *
	 * @param publishCv        true si la case "Publier en tant que CV" est cochée
	 * @param publishPortfolio true si la case "Publier en tant que Portfolio" est cochée
	 */
	@PostMapping("/{id}/publish")
	public String publishProfile(@PathVariable UUID id,
			@RequestParam(required = false) boolean publishCv,
			@RequestParam(required = false) boolean publishPortfolio,
			RedirectAttributes redirectAttributes) throws UnauthorizedException {
		User currentUser = AuthUtils.getCurrentUser();
		if (currentUser == null) {
			return "redirect:/login";
		}

		try {
			profileService.publishProfile(id, publishCv, publishPortfolio, currentUser);
			if (publishCv || publishPortfolio) {
				redirectAttributes.addFlashAttribute("successMessage", "Profil publié avec succès !");
			} else {
				redirectAttributes.addFlashAttribute("successMessage", "Profil dépublié avec succès !");
			}
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", e.getMessage());
		}

		return "redirect:/profiles/" + id + "/publish";
	}

	/**
	 * (Epic 4 - US-022 / US-027) Affiche la page de publication.
	 * URL : GET /profiles/{id}/publish
	 *
	 * Cette page affiche :
	 * - Les cases à cocher pour activer le CV et/ou le Portfolio
	 * - Les URLs publiques à partager (une fois publié)
	 *
	 * Les URLs publiques sont construites dynamiquement à partir de l'adresse du serveur
	 * (ex: http://localhost:8080/public/cv/jean.dupont).
	 */
	@GetMapping("/{id}/publish")
	public String showPublishPage(@PathVariable UUID id, ModelMap model, HttpServletRequest request) {
		User currentUser = AuthUtils.getCurrentUser();
		if (currentUser == null) {
			return "redirect:/login";
		}

		Profile profile = profileService.getProfileById(id);
		if (!profile.getOwner().getId().equals(currentUser.getId())) {
			return "redirect:/profiles/my-profiles";
		}

		// On construit les URLs complètes à partir de l'adresse du serveur actuel
		String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
		String cvUrl = baseUrl + "/public/cv/" + currentUser.getUsername();
		String portfolioUrl = baseUrl + "/public/portfolio/" + currentUser.getUsername();

		model.addAttribute("profile", profile);
		model.addAttribute("cvUrl", cvUrl);                                               // URL à copier pour le CV
		model.addAttribute("portfolioUrl", portfolioUrl);                                 // URL à copier pour le Portfolio
		model.addAttribute("isPublishedAsCv", profile.isPublishedAsCv());                 // pour pré-cocher la case CV
		model.addAttribute("isPublishedAsPortfolio", profile.isPublishedAsPortfolio());   // pour pré-cocher la case Portfolio
		model.addAttribute("isPublished", profile.isPublishedAsCv() || profile.isPublishedAsPortfolio()); // au moins une vue publiée ?
		return "/profiles/publish";
	}

	/**
	 * (Epic 6 - US-037) Enregistre le slug personnalisé d'un profil.
	 * URL : POST /profiles/{id}/slug
	 *
	 * Le slug est le "surnom" dans l'URL publique du profil.
	 * Exemple : si l'utilisateur saisit "mon-cv-dev", son CV sera accessible à
	 * /public/cv/mon-cv-dev au lieu de /public/cv/jdupont.
	 *
	 * Le nettoyage du slug (minuscules, tirets, pas de caractères spéciaux)
	 * est fait automatiquement par ProfileService.updateSlug().
	 * Si le slug est vide, il est désactivé (on revient au username par défaut).
	 *
	 * Ce formulaire est séparé du formulaire principal d'édition car le slug
	 * a sa propre validation (vérification d'unicité, nettoyage spécifique).
	 * Si on les fusionnait, une erreur de slug annulerait aussi les autres modifications.
	 */
	@PostMapping("/{id}/slug")
	public String updateSlug(@PathVariable UUID id,
			@RequestParam(required = false) String slug,
			RedirectAttributes redirectAttributes) throws UnauthorizedException {
		User currentUser = AuthUtils.getCurrentUser();
		if (currentUser == null) return "redirect:/login";

		try {
			profileService.updateSlug(id, slug, currentUser);
			redirectAttributes.addFlashAttribute("successMessage", "Slug mis à jour avec succès !");
		} catch (IllegalArgumentException e) {
			// Le slug est déjà pris par un autre profil ou invalide après nettoyage
			redirectAttributes.addFlashAttribute("error", e.getMessage());
		}
		// On revient sur la page d'édition pour que l'utilisateur voie le résultat
		return "redirect:/profiles/" + id + "/edit";
	}

	/**
	 * (Epic 6 - US-036) Affiche la boîte de réception des messages de contact.
	 * URL : GET /profiles/{id}/messages
	 *
	 * Quand des visiteurs envoient un message via le formulaire de contact d'un portfolio,
	 * les messages sont stockés en base de données. Cette page permet au propriétaire
	 * du profil de les consulter.
	 *
	 * Seul le propriétaire peut accéder à cette page (vérifié dans ProfileService).
	 * Quand le propriétaire ouvre cette page, tous les messages non lus passent en "lus".
	 * C'est comme ouvrir ses emails : une fois affichés, ils ne sont plus "nouveaux".
	 */
	@GetMapping("/{id}/messages")
	public String viewMessages(@PathVariable UUID id, ModelMap model) throws UnauthorizedException {
		User currentUser = AuthUtils.getCurrentUser();
		if (currentUser == null) return "redirect:/login";

		Profile profile = profileService.getProfileById(id);
		// getContactMessages vérifie que c'est bien le propriétaire ET marque les messages comme lus
		List<ContactMessage> messages = profileService.getContactMessages(profile, currentUser);

		model.addAttribute("profile", profile);
		model.addAttribute("messages", messages);
		// hasMessages = true si la liste n'est pas vide → le template affiche soit les messages,
		// soit un message "Aucun message" (grâce aux blocs {{#hasMessages}} et {{^hasMessages}})
		model.addAttribute("hasMessages", !messages.isEmpty());
		return "/profiles/messages";
	}

	/**
	 * (Epic 4 - US-023) Affiche les statistiques de vues d'un profil.
	 * URL : GET /profiles/{id}/stats
	 *
	 * Récupère et envoie au template HTML :
	 * - Le total des vues
	 * - Les vues CV séparément
	 * - Les vues Portfolio séparément
	 * - L'historique des visites récentes
	 */
	@GetMapping("/{id}/stats")
	public String profileStats(@PathVariable UUID id, ModelMap model) {
		User currentUser = AuthUtils.getCurrentUser();
		if (currentUser == null) {
			return "redirect:/login";
		}

		Profile profile = profileService.getProfileById(id);
		if (!profile.getOwner().getId().equals(currentUser.getId())) {
			return "redirect:/profiles/my-profiles";
		}

		long totalViews = profileService.getTotalViews(profile);
		long cvViews = profileService.getCvViews(profile);
		long portfolioViews = profileService.getPortfolioViews(profile);
		List<ProfileView> recentViews = profileService.getRecentViews(profile);

		model.addAttribute("profile", profile);
		model.addAttribute("totalViews", totalViews);
		model.addAttribute("cvViews", cvViews);
		model.addAttribute("portfolioViews", portfolioViews);
		model.addAttribute("recentViews", recentViews);
		model.addAttribute("hasViews", totalViews > 0); // false si jamais vu → affiche un message spécial
		return "/profiles/stats";
	}
}