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

/**
 * CustomizationController — Gère toute la personnalisation visuelle d'un profil.
 *
 * C'est le contrôleur de la page "Personnaliser" accessible depuis "Mes profils".
 * Il permet à l'utilisateur de :
 *   - Choisir un template (mise en page) pour son CV et son Portfolio (US-024)
 *   - Choisir une couleur pour chaque vue (US-025 / US-028)
 *   - Voir un aperçu en temps réel dans le navigateur (US-029)
 *   - Uploader une photo de profil (US-030)
 *   - Ajouter des images à ses projets dans la vue Portfolio (US-031)
 *
 * Toutes les routes commencent par /profiles/{id}/customize.
 * Seul le propriétaire du profil peut accéder à ces pages.
 *
 * Créé dans Epic 5.
 */
@RequestMapping("/profiles/{id}/customize")
@Controller
public class CustomizationController {

    /** Le service principal pour la gestion des profils */
    @Autowired
    private ProfileService profileService;

    /** Le service qui gère l'enregistrement des fichiers images sur le serveur */
    @Autowired
    private ImageUploadService imageUploadService;

    /** Le service pour accéder et modifier les items (expériences, projets…) */
    @Autowired
    private ItemService itemService;

    /**
     * (US-024 / US-025 / US-028 / US-029) Affiche la page de personnalisation.
     * URL : GET /profiles/{id}/customize
     *
     * Prépare et envoie au template HTML :
     * - Le profil à personnaliser
     * - La liste des templates disponibles
     * - La palette de 12 couleurs prédéfinies
     * - Les IDs des templates actuellement sélectionnés (pour pré-cocher les bons)
     */
    @GetMapping
    public String showCustomize(@PathVariable UUID id, ModelMap model) {
        User currentUser = AuthUtils.getCurrentUser();
        if (currentUser == null) return "redirect:/login";

        Profile profile = profileService.getProfileById(id);
        // Sécurité : seul le propriétaire peut accéder à la personnalisation
        if (!profile.getOwner().getId().equals(currentUser.getId())) {
            return "redirect:/profiles/my-profiles";
        }

        List<Template> templates = profileService.getAllTemplates();

        // Les 12 couleurs de la palette prédéfinie (format hexadécimal)
        List<String> palette = List.of(
            "#2c3e50", "#2980b9", "#27ae60", "#8e44ad",
            "#c0392b", "#d35400", "#16a085", "#f39c12",
            "#667eea", "#764ba2", "#1a1a2e", "#e74c3c"
        );

        model.addAttribute("profile", profile);
        model.addAttribute("templates", templates);
        model.addAttribute("palette", palette);
        // On indique quels templates sont actuellement actifs (pour les marquer visuellement)
        model.addAttribute("currentCvTemplateId", profile.getTemplate() != null ? profile.getTemplate().getId() : null);
        model.addAttribute("currentPortfolioTemplateId", profile.getTemplate1() != null ? profile.getTemplate1().getId() : null);
        return "/profiles/customize";
    }

    /**
     * (US-025 / US-028) Sauvegarde la couleur choisie pour une vue.
     * URL : POST /profiles/{id}/customize/color
     *
     * @param color    La couleur au format hex (ex: "#2980b9")
     * @param viewType "cv" ou "portfolio" — indique pour quelle vue on change la couleur
     */
    @PostMapping("/color")
    public String saveColor(@PathVariable UUID id,
            @RequestParam String color,
            @RequestParam String viewType,
            RedirectAttributes redirectAttributes) throws UnauthorizedException {
        User currentUser = AuthUtils.getCurrentUser();
        if (currentUser == null) return "redirect:/login";

        profileService.setColor(id, color, viewType, currentUser);
        redirectAttributes.addFlashAttribute("successMessage", "Couleur enregistrée !");
        // Redirige vers la page de personnalisation (avec le message de succès)
        return "redirect:/profiles/" + id + "/customize";
    }

    /**
     * (US-024) Sauvegarde le template choisi pour une vue.
     * URL : POST /profiles/{id}/customize/template
     *
     * @param templateId L'identifiant du template choisi
     * @param viewType   "cv" ou "portfolio"
     */
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

    /**
     * (US-030) Gère l'upload de la photo de profil.
     * URL : POST /profiles/{id}/customize/upload-photo
     *
     * Étapes :
     * 1. ImageUploadService sauvegarde le fichier sur le disque
     * 2. ProfileService enregistre l'URL de l'image dans le profil en base
     *
     * En cas d'erreur (fichier trop grand, pas une image…), on affiche le message d'erreur.
     *
     * @param photo Le fichier image envoyé depuis le formulaire HTML (enctype="multipart/form-data")
     */
    @PostMapping("/upload-photo")
    public String uploadPhoto(@PathVariable UUID id,
            @RequestParam MultipartFile photo,
            RedirectAttributes redirectAttributes) throws UnauthorizedException {
        User currentUser = AuthUtils.getCurrentUser();
        if (currentUser == null) return "redirect:/login";

        try {
            String url = imageUploadService.uploadProfilePhoto(photo); // sauvegarde le fichier
            profileService.setProfilePhoto(id, url, currentUser);      // enregistre l'URL
            redirectAttributes.addFlashAttribute("successMessage", "Photo mise à jour !");
        } catch (IOException | IllegalArgumentException e) {
            // En cas d'erreur, on affiche le message à l'utilisateur
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/profiles/" + id + "/customize";
    }

    /**
     * (US-031) Gère l'upload d'une image pour un item de projet (vue Portfolio).
     * URL : POST /profiles/{id}/customize/items/{itemId}/upload-image
     *
     * @param itemId L'identifiant de l'item auquel on associe l'image
     * @param image  Le fichier image envoyé depuis le formulaire
     */
    @PostMapping("/items/{itemId}/upload-image")
    public String uploadItemImage(@PathVariable UUID id,
            @PathVariable UUID itemId,
            @RequestParam MultipartFile image,
            RedirectAttributes redirectAttributes) {
        User currentUser = AuthUtils.getCurrentUser();
        if (currentUser == null) return "redirect:/login";

        try {
            String url = imageUploadService.uploadItemImage(image); // sauvegarde le fichier
            itemService.setItemImage(itemId, url);                  // enregistre l'URL sur l'item
            redirectAttributes.addFlashAttribute("successMessage", "Image ajoutée !");
        } catch (IOException | IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/profiles/" + id + "/customize";
    }
}
