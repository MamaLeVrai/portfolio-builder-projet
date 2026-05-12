package alt.portfolio.builder.dtos;

import java.util.UUID;
import alt.portfolio.builder.entities.Profile;
import lombok.Data;

/**
 * DTO (Data Transfer Object) pour afficher un profil dans les vues de consultation.
 *
 * Très similaire à ProfileDisplayDto, ce DTO contient les infos du profil
 * enrichies de l'ID du propriétaire pour les redirections.
 *
 * La différence avec ProfileDisplayDto est sémantique : celui-ci est utilisé
 * pour les vues de "consultation" (lecture seule) alors que ProfileDisplayDto
 * est plus général.
 */
@Data
public class ProfileViewDto {

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
     * Méthode de fabrique (factory method) : crée un ProfileViewDto depuis un Profile.
     *
     * @param profile        L'entité Profile à transformer en DTO
     * @param contextOwnerId L'ID du propriétaire pour les URLs de redirection
     * @return Le DTO prêt à envoyer au template
     */
    public static ProfileViewDto fromProfile(Profile profile, UUID contextOwnerId) {
        ProfileViewDto dto = new ProfileViewDto();
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
