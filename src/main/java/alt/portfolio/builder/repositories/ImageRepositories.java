package alt.portfolio.builder.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import alt.portfolio.builder.entities.Image;

@Repository
public interface ImageRepositories extends JpaRepository<Image, UUID> {

}
