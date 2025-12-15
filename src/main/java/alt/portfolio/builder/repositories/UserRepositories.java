package alt.portfolio.builder.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import alt.portfolio.builder.entities.User;

@Repository
public interface UserRepositories extends JpaRepository<User, UUID> {
	
	public Optional<User>findByUsername(String username);
}
