package alt.portfolio.builder.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import alt.portfolio.builder.entities.Item;

@Repository
public interface ItemRepositories extends JpaRepository<Item, UUID> {

}
