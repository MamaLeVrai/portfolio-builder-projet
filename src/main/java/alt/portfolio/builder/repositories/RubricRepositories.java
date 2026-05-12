package alt.portfolio.builder.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import alt.portfolio.builder.entities.Rubric;

/**
 * Dépôt (repository) pour accéder aux rubriques en base de données.
 *
 * Hérite de JpaRepository et offre automatiquement toutes les opérations CRUD :
 * - findAll() : liste toutes les rubriques
 * - findById(id) : trouve une rubrique par son ID
 * - save(rubric) : enregistre ou met à jour une rubrique
 * - deleteById(id) : supprime une rubrique (et tous ses items grâce à CascadeType.ALL)
 *
 * Les rubriques sont généralement accédées via leur profil (profile.getRubrics()),
 * donc peu de méthodes de recherche spécifiques sont nécessaires ici.
 */
@Repository
public interface RubricRepositories extends JpaRepository<Rubric, UUID> {

}
