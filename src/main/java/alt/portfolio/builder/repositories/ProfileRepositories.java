package alt.portfolio.builder.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import alt.portfolio.builder.entities.Profile;
import alt.portfolio.builder.entities.User;

@Repository
public interface ProfileRepositories extends JpaRepository<Profile, UUID> {

	// public Optional<Profile> findByUserId(UUID userId);

	Optional<Profile> findByName(String name);

	// retourne tous les profils non archivés
	List<Profile> findByArchivedFalse();

	// retourne les profils non archivés d'un utilisateur donné
	List<Profile> findByOwnerIdAndArchivedFalse(UUID ownerId);

	// retourne les profils d'un utilisateur triés par date de mise à jour (US-007)
	List<Profile> findByOwnerOrderByUpdatedAtDesc(User owner);

	// retourne les profils non archivés d'un utilisateur triés par date de mise à jour
	List<Profile> findByOwnerAndArchivedFalseOrderByUpdatedAtDesc(User owner);

	// trouver le profil par défaut d'un utilisateur
	Optional<Profile> findByOwnerAndIsDefaultTrue(User owner);

	// trouver le profil par défaut publié en CV d'un utilisateur
	Optional<Profile> findByOwnerAndIsDefaultTrueAndPublishedAsCvTrue(User owner);

	// trouver le profil par défaut publié en portfolio d'un utilisateur
	Optional<Profile> findByOwnerAndIsDefaultTrueAndPublishedAsPortfolioTrue(User owner);
}