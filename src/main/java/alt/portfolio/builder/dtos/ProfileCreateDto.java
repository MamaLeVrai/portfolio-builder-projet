package alt.portfolio.builder.dtos;

import alt.portfolio.builder.entities.Profile;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProfileCreateDto {
	
	@NotBlank(message = "Le nom du profil est obligatoire")
	@Size(max = 150, message = "Le nom ne doit pas dépasser 150 caractères")
	private String name;
	
	@Size(max = 500, message = "La description ne doit pas dépasser 500 caractères")
	private String description = "";
	
	@Size(max = 255, message = "L'URL de l'image ne doit pas dépasser 255 caractères")
	private String imageUrl;
	
	public Profile toProfile(Profile profile) {
		profile.setName(name);
		profile.setDescription(description != null ? description : "");
		profile.setImageUrl(imageUrl);
		profile.setStatus("draft");
		profile.setDefault(false);
		return profile;
	}
}
