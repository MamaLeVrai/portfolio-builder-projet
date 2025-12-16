package alt.portfolio.builder.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import alt.portfolio.builder.entities.Profile;

@Repository
public interface ProfileRepositories extends JpaRepository<Profile, UUID> {

	// public Optional<Profile> findByUserId(UUID userId);

	Optional<Profile> findByName(String name);

	// retourne tous les profils non archivés
	List<Profile> findByArchivedFalse();
}