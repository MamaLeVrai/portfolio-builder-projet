package alt.portfolio.builder.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import alt.portfolio.builder.entities.User;
import alt.portfolio.builder.repositories.UserRepositories;

@Service
public class DbUserServices implements UserDetailsService{
	
	@Autowired
	private UserRepositories uRepo;
	
	@Autowired
	private PasswordEncoder pEncoder;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<User>optUser = uRepo.findByUsername(username);
		return optUser.orElseThrow(() -> new UsernameNotFoundException("Utilisateur introuvable: " + username));
	}
	
	public void encodePassword(User user) {
		user.setPassword(pEncoder.encode(user.getPassword()));
	}
	
    public User createUser(String login, String password) {
        User user = new User();
        user.setFirstname(login);
        user.setLastname(login);
        user.setEmail(login + "@example.com");
        user.setUsername(login);
        user.setPassword(password);
        encodePassword(user);
        return uRepo.save(user);
    }
}
