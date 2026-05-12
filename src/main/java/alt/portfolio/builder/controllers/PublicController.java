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

/**
 * PublicController — Gère les pages visibles par tout le monde, SANS connexion.
 *
 * C'est la vitrine du portfolio ! N'importe qui sur Internet peut accéder
 * aux pages gérées ici, même sans compte. Elles sont accessibles via :
 *   - /public/cv/{username}        → le CV public de l'utilisateur
 *   - /public/portfolio/{username} → le Portfolio public de l'utilisateur
 *
 * Chaque visite est enregistrée pour les statistiques (US-023).
 *
 * Créé dans Epic 4 (US-021, US-023, US-027).
 * Les URLs /public/** sont autorisées sans connexion dans SecurityConfig.
 */
@RequestMapping("/public")
@Controller
public class PublicController {

    /** Le service qui contient toute la logique des profils */
    @Autowired
    private ProfileService profileService;

    /**
     * (US-027) Affiche le CV public d'un utilisateur.
     * URL : /public/cv/{username}  (ex: /public/cv/jean.dupont)
     *
     * Étapes :
     * 1. On cherche le profil par défaut de cet utilisateur publié en CV
     * 2. On enregistre la visite (pour les statistiques)
     * 3. On affiche la page /public/cv.html
     *
     * @param username Le pseudo de l'utilisateur dont on veut voir le CV
     * @param request  Utilisé pour récupérer l'adresse IP du visiteur
     */
    @GetMapping("/cv/{username}")
    public String viewCv(@PathVariable String username, ModelMap model, HttpServletRequest request) throws EntityNotFoundException {
        // Cherche le profil CV publié de cet utilisateur
        Profile profile = profileService.getPublicCvProfile(username);

        // Enregistre la visite dans la base de données
        profileService.recordView(profile, "cv", request.getRemoteAddr());

        // Envoie les données au template HTML
        model.addAttribute("profile", profile);
        model.addAttribute("owner", profile.getOwner());
        model.addAttribute("mode", "cv");
        model.addAttribute("isCvMode", true);
        model.addAttribute("isPortfolioMode", false);
        return "/public/cv";
    }

    /**
     * (US-027) Affiche le Portfolio public d'un utilisateur.
     * URL : /public/portfolio/{username}  (ex: /public/portfolio/jean.dupont)
     *
     * Même logique que viewCv, mais pour la vue Portfolio.
     */
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
