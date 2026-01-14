package alt.portfolio.builder.dtos;

import alt.portfolio.builder.entities.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRegisterDto {
	
	@NotBlank(message = "Le prénom est obligatoire")
	@Size(max = 45, message = "Le prénom ne doit pas dépasser 45 caractères")
	private String firstname;
	
	@NotBlank(message = "Le nom est obligatoire")
	@Size(max = 45, message = "Le nom ne doit pas dépasser 45 caractères")
	private String lastname;
	
	@NotBlank(message = "Le nom d'utilisateur est obligatoire")
	@Size(max = 45, message = "Le nom d'utilisateur ne doit pas dépasser 45 caractères")
	private String username;
	
	@NotBlank(message = "L'email est obligatoire")
	@Email(message = "L'email doit être valide")
	@Size(max = 45, message = "L'email ne doit pas dépasser 45 caractères")
	private String email;
	
	@NotBlank(message = "Le mot de passe est obligatoire")
	@Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères")
	private String password;
	
	@NotBlank(message = "La confirmation du mot de passe est obligatoire")
	private String confirmPassword;
	
	public User toUser(User user) {
		user.setFirstname(firstname);
		user.setLastname(lastname);
		user.setUsername(username);
		user.setEmail(email);
		user.setPassword(password);
		return user;
	}
}
