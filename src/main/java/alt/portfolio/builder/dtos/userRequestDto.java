package alt.portfolio.builder.dtos;

import alt.portfolio.builder.entities.User;
import lombok.Data;

@Data
public class userRequestDto {
	private String firstname;
	private String lastname;
	private String username;
	private String email;
	private String password;
	
	public User toUser(User user) {
		user.setFirstname(firstname);
		user.setLastname(lastname);
		user.setUsername(username);
		user.setEmail(email);
		user.setPassword(password); 
		return user;
	}

}