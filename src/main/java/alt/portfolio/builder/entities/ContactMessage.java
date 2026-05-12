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
 * (Epic 6 - US-036) Entité représentant un message de contact reçu via le portfolio public.
 *
 * Quand un visiteur remplit le formulaire de contact sur le portfolio d'un utilisateur,
 * le message est stocké ici en base de données.
 *
 * Choix d'implémentation : stockage en base de données.
 * -- Alternative non retenue : envoi par email (nécessiterait une configuration SMTP,
 *    un serveur mail, des identifiants secrets → beaucoup plus complexe à mettre en place).
 * -- Alternative non retenue : notification temps réel via WebSocket → encore plus complexe.
 * -- Choix retenu : table en base de données, simple à implémenter et suffisant pour le projet.
 */
@Entity
@Getter
@Setter
public class ContactMessage {

    /** Identifiant unique du message, généré automatiquement */
    @Id
    private UUID id = UUID.randomUUID();

    /**
     * Le profil portfolio sur lequel le message a été envoyé.
     * Un profil peut recevoir plusieurs messages (OneToMany côté Profile, ManyToOne ici).
     */
    @ManyToOne(optional = false)
    private Profile profile;

    /** Nom de la personne qui a envoyé le message */
    @Column(length = 100, nullable = false)
    private String senderName;

    /** Email de la personne qui a envoyé le message (pour pouvoir lui répondre) */
    @Column(length = 100, nullable = false)
    private String senderEmail;

    /** Contenu du message (max 2000 caractères) */
    @Column(length = 2000, nullable = false)
    private String message;

    /** Date et heure d'envoi du message, remplie automatiquement */
    @CreationTimestamp
    private LocalDateTime sentAt;

    /**
     * Indique si le propriétaire du portfolio a lu ce message.
     * false = non lu (nouveau message), true = lu.
     */
    private boolean read = false;
}
