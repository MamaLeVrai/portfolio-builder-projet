package alt.portfolio.builder.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import alt.portfolio.builder.entities.Category;

/**
 * Dépôt (repository) pour accéder aux catégories en base de données.
 *
 * En étendant JpaRepository, on hérite automatiquement de toutes les opérations
 * de base sans écrire de code :
 * - findAll() : récupère toutes les catégories
 * - findById(id) : récupère une catégorie par son ID
 * - save(category) : enregistre ou met à jour une catégorie
 * - deleteById(id) : supprime une catégorie
 *
 * <Category, UUID> signifie : "ce repository gère des objets Category avec des ID de type UUID"
 */
@Repository
public interface CategoryRepositories extends JpaRepository<Category, UUID> {

}
