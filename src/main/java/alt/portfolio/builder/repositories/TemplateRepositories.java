package alt.portfolio.builder.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import alt.portfolio.builder.entities.Template;

@Repository
public interface TemplateRepositories extends JpaRepository<Template, UUID> {
	
	public Optional<Template> findByName(String name);
}
