package alt.portfolio.builder.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import alt.portfolio.builder.entities.Profile;
import alt.portfolio.builder.entities.User;

/**
 * ProfileRepositories — Le "tiroir" pour les profils.
 *
 * C'est l'intermédiaire entre notre code Java et la table "profile"
 * en base de données. On lui demande des profils, il fait le SQL tout seul.
 * Les noms des méthodes suivent une convention magique de Spring :
 * "findBy" + nom du champ + condition → Spring génère la requête SQL.
 *
 * Modifié dans Epic 4 (US-021 à US-027) : ajout des méthodes pour
 * récupérer les profils publiés en CV ou en Portfolio.
 */
@Repository
public interface ProfileRepositories extends JpaRepository<Profile, UUID> {

    /** Cherche un profil par son nom exact (utilisé pour éviter les doublons) */
    Optional<Profile> findByName(String name);

    /** Récupère tous les profils qui ne sont pas archivés (= "supprimés") */
    List<Profile> findByArchivedFalse();

    /** Récupère les profils non archivés d'un utilisateur précis, identifié par son ID */
    List<Profile> findByOwnerIdAndArchivedFalse(UUID ownerId);

    /** Récupère les profils d'un utilisateur triés du plus récemment modifié au plus ancien (US-007) */
    List<Profile> findByOwnerOrderByUpdatedAtDesc(User owner);

    /** Même chose mais en excluant les profils archivés — utilisé dans "Mes profils" */
    List<Profile> findByOwnerAndArchivedFalseOrderByUpdatedAtDesc(User owner);

    /** Trouve le profil marqué comme "par défaut" pour un utilisateur (il ne peut y en avoir qu'un) */
    Optional<Profile> findByOwnerAndIsDefaultTrue(User owner);

    /**
     * (Epic 4 - US-027) Trouve le profil par défaut d'un utilisateur qui est publié en CV.
     * Utilisé pour afficher la page publique /public/cv/{username}.
     */
    Optional<Profile> findByOwnerAndIsDefaultTrueAndPublishedAsCvTrue(User owner);

    /**
     * (Epic 4 - US-027) Trouve le profil par défaut d'un utilisateur qui est publié en Portfolio.
     * Utilisé pour afficher la page publique /public/portfolio/{username}.
     */
    Optional<Profile> findByOwnerAndIsDefaultTrueAndPublishedAsPortfolioTrue(User owner);

    /**
     * (Epic 6 - US-037) Trouve le profil publié en CV qui a ce slug personnalisé.
     * Utilisé par PublicController pour résoudre /public/cv/{slug}.
     */
    Optional<Profile> findBySlugAndPublishedAsCvTrue(String slug);

    /**
     * (Epic 6 - US-037) Trouve le profil publié en Portfolio qui a ce slug personnalisé.
     * Utilisé par PublicController pour résoudre /public/portfolio/{slug}.
     */
    Optional<Profile> findBySlugAndPublishedAsPortfolioTrue(String slug);

    /**
     * (Epic 6 - US-037) Vérifie qu'un slug n'est pas déjà utilisé par un autre profil.
     * Retourne le profil ayant ce slug, s'il existe.
     */
    Optional<Profile> findBySlug(String slug);
}
