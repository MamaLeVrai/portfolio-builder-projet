package alt.portfolio.builder.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import alt.portfolio.builder.entities.Template;

/**
 * Dépôt (repository) pour accéder aux templates visuels en base de données.
 *
 * Hérite de JpaRepository et offre automatiquement toutes les opérations CRUD :
 * - findAll() : liste tous les templates disponibles (classic, modern, minimal, creative)
 * - findById(id) : trouve un template par son ID
 * - save(template) : enregistre ou met à jour un template
 * - count() : compte combien de templates existent (utilisé par TemplateInitializer
 *   pour savoir s'il faut créer les 4 templates de base au démarrage)
 */
@Repository
public interface TemplateRepositories extends JpaRepository<Template, UUID> {

}
