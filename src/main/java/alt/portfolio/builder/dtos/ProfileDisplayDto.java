package alt.portfolio.builder.dtos;

import java.util.UUID;
import alt.portfolio.builder.entities.Profile;
import lombok.Data;

/**
 * DTO pour afficher un profil avec son contexte (ownerId pour les redirections)
 */
@Data
public class ProfileDisplayDto {
    private UUID id;
    private String name;
    private String description;
    private String ownerFirstname;
    private String ownerLastname;
    private UUID contextOwnerId; // L'ID du propriétaire pour les redirections contextuelles

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
