package alt.portfolio.builder.dtos;

import alt.portfolio.builder.entities.Profile;
import lombok.Data;

@Data
public class ProfileRequestDto {
	private String username;
	private String bio;
	private String avatarUrl;
	
	public Profile toProfile(Profile profile) {
		profile.setName(username);
		return profile;
	}
}
