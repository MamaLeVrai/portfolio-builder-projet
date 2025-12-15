package alt.portfolio.builder.services;


import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import alt.portfolio.builder.dtos.userRequestDto;
import alt.portfolio.builder.entities.User;
import alt.portfolio.builder.repositories.UserRepositories;

@Service
public class UserService  {
	
	@Autowired
	private UserRepositories userRepositories;
	
	public List<User> getUsers(){
		return userRepositories.findAll(); 
	}
	
	public User createUser(userRequestDto userRequest) {
		User user = userRequest.toUser(new User());
		return userRepositories.save(user);
	}
    public User getUserById(UUID id) {
        return userRepositories.findById(id)
            .orElseThrow(() -> new RuntimeException("Utilisateur introuvable: " + id));
    }
    public void deleteUser(UUID id) {
        userRepositories.deleteById(id);
    }
}