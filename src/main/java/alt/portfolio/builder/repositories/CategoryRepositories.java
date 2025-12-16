package alt.portfolio.builder.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import alt.portfolio.builder.entities.Category;
@Repository
public interface CategoryRepositories extends JpaRepository<Category, UUID>{
	
	public Optional<Category> findByName(String name);
}
