package alt.portfolio.builder.controllers;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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
 * ContactController — Reçoit les messages envoyés via le formulaire de contact du portfolio.
 *
 * Quand un visiteur remplit le formulaire de contact sur un portfolio public et clique
 * "Envoyer", la requête arrive ici. Ce contrôleur :
 *   1. Vérifie que tous les champs sont remplis
 *   2. Crée un objet ContactMessage et le sauvegarde en base de données
 *   3. Redirige le visiteur vers le portfolio avec un message de confirmation
 *
 * Ce contrôleur est sous /public/ → aucune connexion requise.
 * C'est voulu : un visiteur non connecté doit pouvoir contacter le propriétaire.
 * La règle qui autorise /public/** sans connexion est dans SecurityConfig.
 *
 * (Epic 6 - US-036) — Formulaire de contact sur le portfolio public.
 *
 * Pourquoi stocker le message en base et pas envoyer un email directement ?
 * -- Alternative non retenue : JavaMailSender (Spring Mail) → envoyer un vrai email SMTP.
 *    Nécessite un serveur mail configuré, des identifiants secrets dans application.properties,
 *    des tests de délivrabilité. Trop complexe à mettre en place et à maintenir.
 * -- Alternative non retenue : service tiers (SendGrid, Mailgun) → API payante, clé à gérer.
 * -- Choix retenu : on crée un ContactMessage en base de données. Le propriétaire le lit
 *    depuis son tableau de bord. Simple, sans configuration externe.
 */
@RequestMapping("/public")   // Toutes les routes de ce contrôleur commencent par /public/
@Controller
public class ContactController {

    /**
     * ProfileService — pour récupérer le profil à partir de son ID.
     * On a besoin du profil pour savoir à qui appartient ce portfolio.
     */
    @Autowired
    private ProfileService profileService;

    /**
     * ContactMessageRepositories — pour sauvegarder le message en base de données.
     * On appelle directement le repository ici car la logique est simple
     * (pas besoin de passer par un service dédié pour juste un save).
     */
    @Autowired
    private ContactMessageRepositories contactMessageRepositories;

    /**
     * Reçoit et enregistre un message de contact envoyé depuis un portfolio public.
     * URL : POST /public/contact/{profileId}
     *
     * Ce formulaire est affiché dans portfolio.html (page publique).
     * Quand le visiteur clique "Envoyer", le navigateur envoie une requête POST ici.
     *
     * Après traitement, on redirige toujours vers le portfolio (GET).
     * C'est le pattern "POST → Redirect → GET" : ça évite le double envoi du
     * formulaire si le visiteur appuie sur F5 pour rafraîchir la page.
     *
     * @param profileId   L'UUID du profil portfolio (dans l'URL, ex: /public/contact/550e8400-...)
     * @param senderName  Le nom du visiteur, venu du champ "name" du formulaire HTML
     * @param senderEmail L'email du visiteur, venu du champ "email" du formulaire HTML
     * @param message     Le contenu du message, venu du champ "message" du formulaire HTML
     * @param redirectAttributes Permet d'envoyer un message à la page suivante après redirection
     */
    @PostMapping("/contact/{profileId}")
    public String submitContact(
            @PathVariable UUID profileId,
            @RequestParam String senderName,
            @RequestParam String senderEmail,
            @RequestParam String message,
            RedirectAttributes redirectAttributes) {

        // Étape 1 : on récupère le profil visé
        // Si l'ID n'existe pas en base, getProfileById lance une exception
        // et Spring affiche une erreur (le visiteur a trafiqué l'URL).
        Profile profile = profileService.getProfileById(profileId);

        // Étape 2 : validation des champs obligatoires
        // isBlank() = vrai si la chaîne est vide OU ne contient que des espaces.
        // On redirige avec un message d'erreur si un champ manque.
        if (senderName == null || senderName.isBlank()
                || senderEmail == null || senderEmail.isBlank()
                || message == null || message.isBlank()) {
            redirectAttributes.addFlashAttribute("contactError", "Tous les champs sont obligatoires.");
            // On redirige vers le portfolio : le visiteur voit son erreur et peut corriger.
            return "redirect:/public/portfolio/" + profile.getOwner().getUsername();
        }

        // Étape 3 : construction du message de contact
        ContactMessage contactMessage = new ContactMessage();
        contactMessage.setProfile(profile);
        contactMessage.setSenderName(senderName.trim());     // trim() enlève les espaces en début/fin
        contactMessage.setSenderEmail(senderEmail.trim());
        // On tronque le message à 2000 caractères pour éviter les abus (quelqu'un qui
        // copie-colle un livre entier). La colonne en base est limitée à 2000 caractères.
        String texte = message.length() > 2000 ? message.substring(0, 2000) : message.trim();
        contactMessage.setMessage(texte);

        // Étape 4 : sauvegarde en base de données
        // La date d'envoi (sentAt) est remplie automatiquement par @CreationTimestamp.
        // Le champ "read" est false par défaut (message non lu).
        contactMessageRepositories.save(contactMessage);

        // Étape 5 : redirection vers le portfolio avec un message de confirmation personnalisé
        // addFlashAttribute = le message survit à la redirection (stocké en session 1 requête).
        // On inclut le prénom du propriétaire pour un message plus chaleureux.
        redirectAttributes.addFlashAttribute("contactSuccess",
                "Votre message a bien été envoyé ! "
                + profile.getOwner().getFirstname()
                + " vous répondra dès que possible.");
        return "redirect:/public/portfolio/" + profile.getOwner().getUsername();
    }
}
