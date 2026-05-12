package alt.portfolio.builder.entities;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

/**
 * ContactMessage — Un message envoyé par un visiteur sur un portfolio public.
 *
 * Imagine qu'un visiteur voit le portfolio de quelqu'un et veut le contacter.
 * Il remplit un petit formulaire avec son nom, son email et un message.
 * Quand il clique "Envoyer", on crée un objet ContactMessage et on le range
 * dans la base de données. Le propriétaire du portfolio peut ensuite aller
 * lire ses messages dans son tableau de bord.
 *
 * C'est comme une boîte aux lettres numérique : le visiteur glisse un message,
 * le propriétaire le lit quand il veut.
 *
 * (Epic 6 - US-036) — Formulaire de contact sur le portfolio public.
 *
 * Pourquoi stocker en base de données et pas envoyer un vrai email ?
 * -- Alternative 1 : Spring Mail (JavaMailSender) → envoyer un email SMTP.
 *    Problème : il faut configurer un serveur mail (Gmail, SendGrid…), gérer des mots de passe
 *    dans les fichiers de config, se battre avec les filtres anti-spam. Trop complexe.
 * -- Alternative 2 : Service tiers (SendGrid, Mailgun, AWS SES) → API externe.
 *    Problème : clé API à protéger, dépendance externe, coût potentiel.
 * -- Choix retenu : on stocke simplement en base de données. Le propriétaire consulte
 *    ses messages depuis son espace connecté. Simple, fiable, zéro configuration externe.
 */
@Entity   // Cette classe est une "table" en base de données
@Getter   // Lombok génère automatiquement tous les "getters" (getId, getSenderName, etc.)
@Setter   // Lombok génère automatiquement tous les "setters" (setId, setSenderName, etc.)
public class ContactMessage {

    /**
     * Identifiant unique de ce message.
     * UUID = une suite de chiffres et lettres aléatoires qui garantit l'unicité.
     * Ex : "550e8400-e29b-41d4-a716-446655440000"
     * Chaque message a son propre ID, différent de tous les autres.
     */
    @Id
    private UUID id = UUID.randomUUID();

    /**
     * Le profil portfolio sur lequel ce message a été envoyé.
     *
     * @ManyToOne signifie "plusieurs messages peuvent être liés au même profil".
     * Un profil peut recevoir 100 messages → chacun pointe vers ce même profil.
     * optional = false → un message doit TOUJOURS être lié à un profil (obligatoire).
     */
    @ManyToOne(optional = false)
    private Profile profile;

    /**
     * Le nom de la personne qui a envoyé le message.
     * Ex : "Marie Dupont", "Jean Martin"
     * Limité à 100 caractères, obligatoire (nullable = false).
     */
    @Column(length = 100, nullable = false)
    private String senderName;

    /**
     * L'adresse email de la personne qui a envoyé le message.
     * Le propriétaire peut cliquer sur cet email pour répondre directement.
     * Ex : "marie.dupont@gmail.com"
     * Limité à 100 caractères, obligatoire.
     */
    @Column(length = 100, nullable = false)
    private String senderEmail;

    /**
     * Le contenu du message écrit par le visiteur.
     * Limité à 2000 caractères pour éviter les abus
     * (quelqu'un qui colle un roman entier dans le formulaire).
     * Obligatoire : un message vide n'a pas de sens.
     */
    @Column(length = 2000, nullable = false)
    private String message;

    /**
     * La date et l'heure exactes auxquelles le message a été envoyé.
     * @CreationTimestamp = Hibernate remplit ce champ automatiquement
     * au moment où on sauvegarde l'objet en base. On n'a pas à le calculer nous-mêmes.
     * Ex : "2026-05-12T14:30:00"
     */
    @CreationTimestamp
    private LocalDateTime sentAt;

    /**
     * Est-ce que le propriétaire a déjà lu ce message ?
     * false = nouveau message, pas encore lu (comme une enveloppe fermée).
     * true  = message lu (comme une enveloppe ouverte).
     *
     * Par défaut à false : quand un message arrive, il est "non lu".
     * Passe à true quand le propriétaire consulte la page /profiles/{id}/messages.
     */
    private boolean read = false;
}
