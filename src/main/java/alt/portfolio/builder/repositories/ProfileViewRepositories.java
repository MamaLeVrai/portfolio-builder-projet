package alt.portfolio.builder.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import alt.portfolio.builder.entities.Profile;
import alt.portfolio.builder.entities.ProfileView;

@Repository
public interface ProfileViewRepositories extends JpaRepository<ProfileView, UUID> {

	List<ProfileView> findByProfileOrderByViewedAtDesc(Profile profile);

	long countByProfile(Profile profile);

	long countByProfileAndViewType(Profile profile, String viewType);
}
