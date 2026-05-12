package alt.portfolio.builder.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import alt.portfolio.builder.entities.ContactMessage;
import alt.portfolio.builder.entities.Profile;

/**
 * (Epic 6 - US-036) Repository pour accéder aux messages de contact en base de données.
 *
 * Hérite de JpaRepository : toutes les opérations de base (save, findById, delete...)
 * sont disponibles automatiquement.
 */
@Repository
public interface ContactMessageRepositories extends JpaRepository<ContactMessage, UUID> {

    /**
     * Récupère tous les messages reçus pour un profil donné, du plus récent au plus ancien.
     * Spring Data génère automatiquement la requête SQL grâce au nom de la méthode.
     *
     * @param profile Le profil dont on veut voir les messages
     * @return La liste des messages triés par date décroissante
     */
    List<ContactMessage> findByProfileOrderBySentAtDesc(Profile profile);

    /**
     * Compte le nombre de messages non lus pour un profil.
     * Utilisé pour afficher un badge "X nouveaux messages" dans l'interface.
     *
     * @param profile Le profil concerné
     * @return Le nombre de messages avec read = false
     */
    long countByProfileAndReadFalse(Profile profile);
}
