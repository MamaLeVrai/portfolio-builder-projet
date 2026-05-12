package alt.portfolio.builder.controllers;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import alt.portfolio.builder.entities.ContactMessage;
import alt.portfolio.builder.entities.Profile;
import alt.portfolio.builder.repositories.ContactMessageRepositories;
import alt.portfolio.builder.services.ProfileService;

/**
 * (Epic 6 - US-036) Contrôleur pour le formulaire de contact du portfolio public.
 *
 * Ce contrôleur reçoit les messages envoyés par les visiteurs depuis la page
 * portfolio publique. Les messages sont stockés en base de données et consultables
 * par le propriétaire du portfolio dans son tableau de bord.
 *
 * Ces routes sont sous /public/ donc accessibles sans connexion (voir SecurityConfig).
 *
 * Choix d'implémentation : stockage en base de données.
 * -- Alternative non retenue : envoi par email via Spring Mail (JavaMailSender).
 *    Cela aurait nécessité une config SMTP dans application.properties, des identifiants
 *    de serveur mail, et des tests de délivrabilité → trop complexe pour ce projet.
 * -- Choix retenu : table ContactMessage en base, simple et suffisant.
 */
@RequestMapping("/public")
@Controller
public class ContactController {

    @Autowired
    private ProfileService profileService;

    @Autowired
    private ContactMessageRepositories contactMessageRepositories;

    /**
     * (US-036) Reçoit et enregistre le message de contact.
     * URL : POST /public/contact/{profileId}
     *
     * Le formulaire est affiché dans portfolio.html. Quand le visiteur l'envoie,
     * on crée un ContactMessage en base de données et on redirige vers une page de confirmation.
     *
     * @param profileId   L'ID du profil portfolio sur lequel le message est envoyé
     * @param senderName  Nom du visiteur (obligatoire)
     * @param senderEmail Email du visiteur (pour que le propriétaire puisse répondre)
     * @param message     Contenu du message
     */
    @PostMapping("/contact/{profileId}")
    public String submitContact(
            @PathVariable UUID profileId,
            @RequestParam String senderName,
            @RequestParam String senderEmail,
            @RequestParam String message,
            RedirectAttributes redirectAttributes) {

        // Vérifie que le profil existe
        Profile profile = profileService.getProfileById(profileId);

        // Validation basique : on vérifie que les champs ne sont pas vides
        if (senderName == null || senderName.isBlank()
                || senderEmail == null || senderEmail.isBlank()
                || message == null || message.isBlank()) {
            redirectAttributes.addFlashAttribute("contactError", "Tous les champs sont obligatoires.");
            return "redirect:/public/portfolio/" + profile.getOwner().getUsername();
        }

        // Crée et sauvegarde le message en base de données
        ContactMessage contactMessage = new ContactMessage();
        contactMessage.setProfile(profile);
        contactMessage.setSenderName(senderName.trim());
        contactMessage.setSenderEmail(senderEmail.trim());
        // Limite le message à 2000 caractères pour éviter les abus
        contactMessage.setMessage(message.length() > 2000 ? message.substring(0, 2000) : message.trim());

        contactMessageRepositories.save(contactMessage);

        // Redirige vers la page portfolio avec un message de succès
        redirectAttributes.addFlashAttribute("contactSuccess",
                "Votre message a bien été envoyé ! " + profile.getOwner().getFirstname() + " vous répondra dès que possible.");
        return "redirect:/public/portfolio/" + profile.getOwner().getUsername();
    }
}
