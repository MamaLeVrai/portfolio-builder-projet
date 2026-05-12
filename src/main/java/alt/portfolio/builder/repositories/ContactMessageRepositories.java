package alt.portfolio.builder.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import alt.portfolio.builder.entities.ContactMessage;
import alt.portfolio.builder.entities.Profile;

/**
 * ContactMessageRepositories — Le tiroir pour les messages de contact.
 *
 * Ce repository est le lien entre notre code Java et la table "contact_message"
 * en base de données. On lui demande des messages, il fait le SQL à notre place.
 *
 * Comment ça marche ?
 * Spring Data JPA lit le NOM de la méthode et génère automatiquement la requête SQL.
 * Exemple : findByProfileOrderBySentAtDesc
 *   → "findBy Profile" = "WHERE profile = ?"
 *   → "OrderBy SentAt Desc" = "ORDER BY sent_at DESC"
 * Pas besoin d'écrire du SQL nous-mêmes !
 *
 * (Epic 6 - US-036) — Messages de contact sur les portfolios publics.
 */
@Repository   // Indique à Spring que c'est un repository (accès base de données)
public interface ContactMessageRepositories extends JpaRepository<ContactMessage, UUID> {
    // JpaRepository<ContactMessage, UUID> nous donne gratuitement :
    // - save(message) → sauvegarder un message
    // - findById(id) → trouver un message par son ID
    // - delete(message) → supprimer un message
    // - findAll() → tous les messages
    // On ajoute nos propres méthodes ci-dessous pour des recherches plus précises.

    /**
     * Récupère tous les messages reçus sur un profil donné,
     * du plus récent (le plus grand sentAt) au plus ancien.
     *
     * SQL généré automatiquement :
     * SELECT * FROM contact_message
     * WHERE profile_id = ?
     * ORDER BY sent_at DESC
     *
     * Exemple d'utilisation : afficher la liste des messages d'un portfolio
     * dans la page /profiles/{id}/messages, les plus récents en premier.
     *
     * @param profile Le profil dont on veut les messages
     * @return La liste de tous les messages pour ce profil, triée par date décroissante
     */
    List<ContactMessage> findByProfileOrderBySentAtDesc(Profile profile);

    /**
     * Compte le nombre de messages NON LUS pour un profil.
     * "ReadFalse" signifie "WHERE read = false".
     *
     * SQL généré automatiquement :
     * SELECT COUNT(*) FROM contact_message
     * WHERE profile_id = ? AND read = false
     *
     * Exemple d'utilisation : afficher un badge "3 nouveaux messages" sur
     * le bouton Messages dans la liste des profils.
     *
     * @param profile Le profil concerné
     * @return Le nombre de messages avec read = false (non lus)
     */
    long countByProfileAndReadFalse(Profile profile);
}
