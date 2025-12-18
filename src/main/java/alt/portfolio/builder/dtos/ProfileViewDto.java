package alt.portfolio.builder.dtos;

import java.util.UUID;
import alt.portfolio.builder.entities.Profile;
import lombok.Data;

/**
 * DTO pour afficher un profil avec son contexte (ownerId pour les redirections)
 */
@Data
public class ProfileViewDto {
    private UUID id;
    private String name;
    private String description;
    private String ownerFirstname;
    private String ownerLastname;
    private UUID contextOwnerId; // L'ID du propriétaire pour les redirections contextuelles

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
