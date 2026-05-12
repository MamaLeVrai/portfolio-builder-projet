package alt.portfolio.builder.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import alt.portfolio.builder.entities.Image;

/**
 * Dépôt (repository) pour accéder aux images en base de données.
 *
 * Hérite de JpaRepository et offre donc automatiquement toutes les opérations CRUD :
 * - findAll() : liste toutes les images
 * - findById(id) : trouve une image par son ID
 * - save(image) : enregistre ou met à jour une image
 * - deleteById(id) : supprime une image
 *
 * Pas de méthodes personnalisées pour l'instant : les opérations de base suffisent.
 */
@Repository
public interface ImageRepositories extends JpaRepository<Image, UUID> {

}
