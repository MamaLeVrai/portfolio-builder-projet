package alt.portfolio.builder.dtos;

import java.util.UUID;

import alt.portfolio.builder.entities.Profile;
import lombok.Data;

@Data
public class ProfileRequestDto {
	private String username;
	private String bio;
	private String avatarUrl;
	private UUID ownerId;
	
	public Profile toProfile(Profile profile) {
		profile.setName(username);
		profile.setDescription(bio != null ? bio : "");
		return profile;
	}
}