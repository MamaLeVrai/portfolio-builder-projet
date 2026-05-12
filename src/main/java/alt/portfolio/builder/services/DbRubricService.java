package alt.portfolio.builder.services;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import alt.portfolio.builder.repositories.RubricRepositories;
import alt.portfolio.builder.entities.Rubric;
import alt.portfolio.builder.entities.Profile;
import alt.portfolio.builder.entities.Category;
import java.util.UUID;
import java.util.List;
import java.util.Optional;

/**
 * Service de base de données pour gérer les rubriques.
 *
 * Une rubrique est une grande section d'un profil (ex : "Expériences", "Formation").
 * Ce service gère toutes les opérations CRUD (créer, lire, modifier, supprimer)
 * ainsi que le changement de visibilité et l'ordre d'affichage des rubriques.
 */
@Service
public class DbRubricService {

    @Autowired
    private RubricRepositories rubricRepositories;

    /**
     * Crée une nouvelle rubrique dans un profil.
     *
     * L'ordre est calculé automatiquement : si le profil a déjà 2 rubriques,
     * la nouvelle aura l'ordre 3 (= 2 + 1). Si c'est la première, elle aura l'ordre 1.
     *
     * @param profile  Le profil auquel ajouter cette rubrique
     * @param category Le type de rubrique (expérience, formation, etc.)
     * @param name     Le titre affiché de la rubrique
     * @return La rubrique créée et sauvegardée
     */
    public Rubric addRubric(Profile profile, Category category, String name) {
        Rubric r = new Rubric();
        r.setProfile(profile);
        r.setCategory(category);
        r.setName(name);
        // Calcule la position de la nouvelle rubrique dans le profil
        if (profile.getRubrics() != null && !profile.getRubrics().isEmpty()) {
            r.setOrder((byte)(profile.getRubrics().size() + 1));
        } else {
            r.setOrder((byte)1); // Première rubrique du profil
        }
        return rubricRepositories.save(r);
    }

    /**
     * Renomme une rubrique existante.
     * On cherche la rubrique par son ID, on change son nom, puis on sauvegarde.
     *
     * @param id      L'identifiant de la rubrique à renommer
     * @param newName Le nouveau nom à donner à la rubrique
     */
    public void updateRubric(UUID id, String newName) {
        Optional<Rubric> opt = rubricRepositories.findById(id);
        if (opt.isPresent()) {
            Rubric r = opt.get();
            r.setName(newName);
            rubricRepositories.save(r);
        }
    }

    /**
     * Supprime définitivement une rubrique et tous ses items.
     * (La suppression en cascade est gérée par l'annotation @OneToMany dans Rubric)
     *
     * @param id L'identifiant de la rubrique à supprimer
     */
    public void deleteRubric(UUID id) {
        rubricRepositories.deleteById(id);
    }

    /**
     * Cache ou affiche une rubrique sur le document public.
     * Si la rubrique est visible (true), elle devient cachée (false), et vice versa.
     * Utile pour préparer du contenu sans l'afficher immédiatement.
     *
     * @param id L'identifiant de la rubrique à basculer
     */
    public void toggleVisibility(UUID id) {
        Optional<Rubric> opt = rubricRepositories.findById(id);
        if (opt.isPresent()) {
            Rubric r = opt.get();
            r.setVisible(!r.isVisible()); // On inverse : true devient false, false devient true
            rubricRepositories.save(r);
        }
    }

    /**
     * Réorganise les rubriques dans un profil selon un nouvel ordre.
     *
     * orderedIds est une liste d'IDs dans le nouvel ordre voulu.
     * On attribue l'ordre 1, 2, 3... à chaque rubrique dans cet ordre.
     * Exemple : si orderedIds = [C, A, B], alors C = ordre 1, A = ordre 2, B = ordre 3.
     *
     * @param orderedIds Liste des IDs des rubriques dans le nouvel ordre voulu
     */
    public void reorderRubrics(List<UUID> orderedIds) {
        for (int i = 0; i < orderedIds.size(); i++) {
            Optional<Rubric> opt = rubricRepositories.findById(orderedIds.get(i));
            if (opt.isPresent()) {
                Rubric r = opt.get();
                r.setOrder((byte)(i + 1)); // i+1 car on commence à 1, pas à 0
                rubricRepositories.save(r);
            }
        }
    }
}
