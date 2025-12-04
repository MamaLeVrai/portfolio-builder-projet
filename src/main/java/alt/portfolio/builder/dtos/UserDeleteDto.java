package alt.portfolio.builder.dtos;

import alt.portfolio.builder.entities.User;
import lombok.Data;

@Data
public class UserDeleteDto {
	private String Id;

	
	public User toUser(User user) {
		user.getId();
		return user;
	}
	
}
