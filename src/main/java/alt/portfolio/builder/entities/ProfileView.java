package alt.portfolio.builder.entities;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * ProfileView — Un enregistrement de visite sur un profil public.
 *
 * Imagine un cahier où on note chaque fois que quelqu'un regarde
 * ton profil. Chaque ligne du cahier, c'est un objet ProfileView.
 *
 * On s'en sert pour les statistiques (Epic 4 - US-023) :
 * savoir combien de personnes ont vu ton CV ou ton Portfolio.
 */
@Entity
@Getter
@Setter
public class ProfileView {

    /** Identifiant unique de cette visite (généré automatiquement, comme un numéro de ticket) */
    @Id
    private UUID id = UUID.randomUUID();

    /**
     * Le profil qui a été visité.
     * Un profil peut avoir beaucoup de visites (un à plusieurs).
     */
    @ManyToOne(optional = false)
    private Profile profile;

    /**
     * Type de vue : "cv" ou "portfolio".
     * Ça nous dit si le visiteur regardait le CV ou le Portfolio.
     */
    @Column(length = 10, nullable = false)
    private String viewType;

    /**
     * L'adresse IP du visiteur (comme l'adresse de sa maison sur Internet).
     * On la garde pour pouvoir repérer les visites répétées.
     */
    @Column(length = 45)
    private String visitorIp;

    /**
     * La date et l'heure exactes de la visite.
     * Rempli automatiquement par Hibernate au moment de la sauvegarde.
     */
    @CreationTimestamp
    private LocalDateTime viewedAt;
}
