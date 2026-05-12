package alt.portfolio.builder.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import alt.portfolio.builder.entities.Item;

/**
 * Dépôt (repository) pour accéder aux items (éléments de rubriques) en base de données.
 *
 * Hérite de JpaRepository et offre automatiquement toutes les opérations CRUD :
 * - findAll() : liste tous les items
 * - findById(id) : trouve un item par son ID
 * - save(item) : enregistre ou met à jour un item
 * - deleteById(id) : supprime un item
 *
 * Les items sont généralement accédés via leur rubrique (rubric.getItems()),
 * donc peu de méthodes de recherche spécifiques sont nécessaires ici.
 */
@Repository
public interface ItemRepositories extends JpaRepository<Item, UUID> {

}
