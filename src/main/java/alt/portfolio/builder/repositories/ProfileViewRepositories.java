package alt.portfolio.builder.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import alt.portfolio.builder.entities.Profile;
import alt.portfolio.builder.entities.ProfileView;

/**
 * ProfileViewRepositories — Le "tiroir" pour les visites de profils.
 *
 * C'est l'intermédiaire entre notre programme Java et la table
 * "profile_view" dans la base de données MySQL.
 * Spring génère automatiquement les requêtes SQL à partir
 * des noms des méthodes (ex: "findByProfile" → "SELECT * WHERE profile = ?").
 *
 * Créé dans Epic 4 (US-023) pour les statistiques de vues.
 */
@Repository
public interface ProfileViewRepositories extends JpaRepository<ProfileView, UUID> {

    /**
     * Récupère toutes les visites d'un profil, de la plus récente à la plus ancienne.
     * Utilisé pour afficher l'historique des visites dans la page de statistiques.
     *
     * @param profile Le profil dont on veut voir les visites
     * @return La liste des visites triées par date décroissante
     */
    List<ProfileView> findByProfileOrderByViewedAtDesc(Profile profile);

    /**
     * Compte le nombre total de visites pour un profil (CV + Portfolio confondus).
     *
     * @param profile Le profil à analyser
     * @return Le nombre total de visites
     */
    long countByProfile(Profile profile);

    /**
     * Compte les visites d'un profil selon le type ("cv" ou "portfolio").
     * Permet de savoir combien de personnes ont vu le CV séparément du Portfolio.
     *
     * @param profile  Le profil à analyser
     * @param viewType "cv" ou "portfolio"
     * @return Le nombre de visites pour ce type
     */
    long countByProfileAndViewType(Profile profile, String viewType);
}
