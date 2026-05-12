package alt.portfolio.builder.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import alt.portfolio.builder.entities.Location;

/**
 * Dépôt (repository) pour accéder aux lieux en base de données.
 *
 * Hérite de JpaRepository et offre automatiquement toutes les opérations CRUD :
 * - findAll() : liste tous les lieux
 * - findById(id) : trouve un lieu par son ID
 * - save(location) : enregistre ou met à jour un lieu
 * - deleteById(id) : supprime un lieu
 *
 * Les lieux sont utilisés lors de la création ou modification d'items de rubriques.
 * Un lieu peut représenter une ville, une entreprise, une école, etc.
 */
@Repository
public interface LocationRepositories extends JpaRepository<Location, UUID> {

}
