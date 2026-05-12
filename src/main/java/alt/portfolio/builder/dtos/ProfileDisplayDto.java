package alt.portfolio.builder.dtos;

import java.util.UUID;
import alt.portfolio.builder.entities.Profile;
import lombok.Data;

/**
 * DTO (Data Transfer Object) pour afficher les informations d'un profil dans les vues.
 *
 * Ce DTO contient les données d'un profil enrichies du contexte d'affichage :
 * en plus des infos du profil, il garde l'ID du propriétaire pour construire
 * les URLs de redirection correctes (ex : /users/{ownerId}/profiles/{profileId}).
 *
 * Utilise une méthode statique fromProfile() pour construire facilement le DTO
 * à partir d'une entité Profile existante.
 */
@Data
public class ProfileDisplayDto {

    /** Identifiant unique du profil */
    private UUID id;

    /** Nom du profil */
    private String name;

    /** Description du profil */
    private String description;

    /** Prénom du propriétaire (pour l'affichage) */
    private String ownerFirstname;

    /** Nom de famille du propriétaire (pour l'affichage) */
    private String ownerLastname;

    /** ID du propriétaire, utilisé pour construire les URLs de navigation */
    private UUID contextOwnerId;

    /**
     * Méthode de fabrique (factory method) : crée un ProfileDisplayDto à partir d'un Profile.
     *
     * @param profile       L'entité Profile dont on veut afficher les infos
     * @param contextOwnerId L'ID du propriétaire pour les redirections
     * @return Le DTO prêt à être envoyé au template HTML
     */
    public static ProfileDisplayDto fromProfile(Profile profile, UUID contextOwnerId) {
        ProfileDisplayDto dto = new ProfileDisplayDto();
        dto.setId(profile.getId());
        dto.setName(profile.getName());
        dto.setDescription(profile.getDescription());
        if (profile.getOwner() != null) {
            dto.setOwnerFirstname(profile.getOwner().getFirstname());
            dto.setOwnerLastname(profile.getOwner().getLastname());
        }
        dto.setContextOwnerId(contextOwnerId);
        return dto;
    }
}
