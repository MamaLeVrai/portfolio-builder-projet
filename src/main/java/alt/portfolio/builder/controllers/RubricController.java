package alt.portfolio.builder.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import alt.portfolio.builder.services.DbRubricService;
import alt.portfolio.builder.repositories.ProfileRepositories;
import alt.portfolio.builder.repositories.CategoryRepositories;
import alt.portfolio.builder.entities.Profile;
import alt.portfolio.builder.entities.Category;
import java.util.UUID;
import java.util.List;

/**
 * Contrôleur pour gérer les rubriques d'un profil.
 *
 * Une rubrique est une section du CV ou portfolio (ex : "Expériences", "Formation", "Projets").
 * Ce contrôleur permet d'ajouter, modifier, supprimer, réordonner et cacher/montrer des rubriques.
 *
 * Toutes les routes commencent par "/rubrics/".
 * Après chaque action, on redirige vers la page du profil concerné.
 */
@RequestMapping("/rubrics")
@Controller
public class RubricController {

    @Autowired
    private DbRubricService rubricService;

    /** Pour récupérer le profil par son ID */
    @Autowired
    private ProfileRepositories profileRepositories;

    /** Pour récupérer la catégorie par son ID */
    @Autowired
    private CategoryRepositories categoryRepositories;

    /**
     * Ajoute une nouvelle rubrique à un profil.
     * URL : POST /rubrics/add
     * Paramètres : profileId (à quel profil), categoryId (quel type de rubrique), name (son nom)
     */
    @PostMapping("/add")
    public String addRubric(@RequestParam UUID profileId, @RequestParam UUID categoryId, @RequestParam String name) {
        // On charge le profil et la catégorie depuis la base de données
        Profile p = profileRepositories.findById(profileId).orElseThrow();
        Category c = categoryRepositories.findById(categoryId).orElseThrow();
        rubricService.addRubric(p, c, name);
        // On retourne à la page du profil après l'ajout
        return "redirect:/profiles/" + profileId;
    }

    /**
     * Renomme une rubrique existante.
     * URL : POST /rubrics/{id}/edit
     * L'id dans l'URL est l'identifiant de la rubrique à modifier.
     */
    @PostMapping("/{id}/edit")
    public String editRubric(@PathVariable UUID id, @RequestParam String name, @RequestParam UUID profileId) {
        rubricService.updateRubric(id, name);
        return "redirect:/profiles/" + profileId;
    }

    /**
     * Supprime définitivement une rubrique et tous ses éléments (items).
     * URL : POST /rubrics/{id}/delete
     */
    @PostMapping("/{id}/delete")
    public String deleteRubric(@PathVariable UUID id, @RequestParam UUID profileId) {
        rubricService.deleteRubric(id);
        return "redirect:/profiles/" + profileId;
    }

    /**
     * Cache ou affiche une rubrique sur le CV/portfolio public.
     * URL : POST /rubrics/{id}/toggle-visibility
     * Si la rubrique est visible, elle devient cachée, et vice versa.
     * Cela permet de préparer une rubrique sans l'afficher immédiatement.
     */
    @PostMapping("/{id}/toggle-visibility")
    public String toggleVisibility(@PathVariable UUID id, @RequestParam UUID profileId) {
        rubricService.toggleVisibility(id);
        return "redirect:/profiles/" + profileId;
    }

    /**
     * Réorganise l'ordre des rubriques sur le profil.
     * URL : POST /rubrics/reorder
     * rubricIds est une liste d'identifiants dans le nouvel ordre voulu.
     * La première rubrique de la liste aura l'ordre 1, la deuxième l'ordre 2, etc.
     */
    @PostMapping("/reorder")
    public String reorder(@RequestParam List<UUID> rubricIds, @RequestParam UUID profileId) {
        rubricService.reorderRubrics(rubricIds);
        return "redirect:/profiles/" + profileId;
    }
}
