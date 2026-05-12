package alt.portfolio.builder.application;

import alt.portfolio.builder.entities.User;
import alt.portfolio.builder.exceptions.EntityNotFoundException;
import alt.portfolio.builder.exceptions.UnauthorizedException;
import alt.portfolio.builder.exceptions.ValidationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.net.URLDecoder;
import java.nio.charset.Charset;

/**
 * Gestionnaire global des erreurs de l'application.
 *
 * Cette classe intercepte toutes les exceptions qui se produisent dans l'application
 * et les transforme en jolies pages d'erreur au lieu d'afficher un message technique.
 *
 * C'est comme un filet de sécurité : si quelque chose se passe mal n'importe où
 * dans l'application, cette classe attrape l'erreur et affiche une page adaptée.
 */
@ControllerAdvice
public class MainControllerAdvice {

    /**
     * Récupère l'utilisateur connecté et le rend disponible sur toutes les pages,
     * y compris les pages d'erreur. Ainsi le menu de navigation reste correct même
     * quand une erreur se produit.
     */
    @ModelAttribute("activeUser")
    public User getActivatedUser() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            // Si personne n'est connecté ou si c'est un visiteur anonyme, on retourne null
            if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
                return null;
            }
            Object principal = auth.getPrincipal();
            if (principal instanceof User) {
                return (User) principal;
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Quand on essaie d'accéder à quelque chose qui n'existe pas (profil supprimé, etc.),
     * on affiche la page d'erreur 404 avec un message explicatif.
     */
    @ExceptionHandler(exception = EntityNotFoundException.class)
    public ModelAndView entityNotFound(EntityNotFoundException ex) {
        return  new ModelAndView("/errors/404","message", ex.getMessage());
    }

    /**
     * Quand un utilisateur essaie de faire quelque chose qu'il n'a pas le droit de faire
     * (accéder au profil d'un autre utilisateur, par exemple),
     * on affiche la page d'erreur 403 "Accès interdit".
     */
    @ExceptionHandler(exception = UnauthorizedException.class)
    public ModelAndView unauthorizedHandler(UnauthorizedException ex) {
        return new ModelAndView("/errors/403", "message", ex.getMessage());
    }

    /**
     * Quand les données envoyées par l'utilisateur sont invalides (email déjà pris, etc.),
     * on affiche la page d'erreur 400 "Mauvaise requête".
     */
    @ExceptionHandler(exception = ValidationException.class)
    public ModelAndView validationExceptionHandler(ValidationException ex) {
        return new ModelAndView("/errors/400", "message", ex.getMessage());
    }

    /**
     * Si une variable null est utilisée par erreur dans le code,
     * on affiche la page 404 plutôt que de planter complètement.
     */
    @ExceptionHandler(exception = NullPointerException.class)
    public ModelAndView nullPointerExceptionHandler() {
        return new ModelAndView("/errors/404","message", "Accès à une instance non créée.");
    }

    /**
     * Quand l'utilisateur visite une URL qui n'existe pas dans l'application,
     * on affiche la page 404 "Page introuvable" avec l'URL demandée.
     */
    @ExceptionHandler(exception = NoHandlerFoundException.class)
    public ModelAndView handle404(NoHandlerFoundException ex) {
        // Décode l'URL pour afficher les caractères spéciaux correctement
        String url = URLDecoder.decode(ex.getRequestURL(), Charset.defaultCharset());
        ModelAndView mv = new ModelAndView("/errors/404","message", "URL non trouvée : " + url);
        User activeUser = getActivatedUser();
        if (activeUser != null) {
            mv.addObject("activeUser", activeUser);
        }
        return mv;
    }
}
