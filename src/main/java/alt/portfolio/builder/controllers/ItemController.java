package alt.portfolio.builder.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import alt.portfolio.builder.services.DbItemService;
import alt.portfolio.builder.repositories.RubricRepositories;
import alt.portfolio.builder.repositories.LocationRepositories;
import alt.portfolio.builder.entities.Rubric;
import alt.portfolio.builder.entities.Location;
import java.util.UUID;
import java.util.List;
import java.time.LocalDate;

/**
 * Contrôleur pour gérer les éléments (items) à l'intérieur des rubriques.
 *
 * Un item est une entrée concrète dans une rubrique :
 * par exemple "Stage chez Google" dans la rubrique "Expériences",
 * ou "Baccalauréat" dans la rubrique "Formation".
 *
 * Chaque item peut avoir un titre, une description, des dates de début/fin et un lieu.
 * Toutes les routes commencent par "/items/".
 */
@RequestMapping("/items")
@Controller
public class ItemController {

    @Autowired
    private DbItemService itemService;

    /** Pour récupérer la rubrique à laquelle appartient l'item */
    @Autowired
    private RubricRepositories rubricRepositories;

    /** Pour récupérer le lieu associé à l'item */
    @Autowired
    private LocationRepositories locationRepositories;

    /**
     * Ajoute un nouvel élément dans une rubrique.
     * URL : POST /items/add
     *
     * Paramètres optionnels : description, startDate, endDate, locationId
     * (ils ne sont pas obligatoires car tous les items ne nécessitent pas ces infos)
     *
     * @DateTimeFormat(iso = DATE) permet de lire les dates au format ISO "2024-01-15"
     * envoyées depuis le formulaire HTML avec <input type="date">
     */
    @PostMapping("/add")
    public String addItem(
        @RequestParam UUID rubricId,
        @RequestParam UUID profileId,
        @RequestParam String title,
        @RequestParam(required=false) String description,
        @RequestParam(required=false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam(required=false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
        @RequestParam(required=false) UUID locationId
    ) {
        // Charge la rubrique depuis la base de données
        Rubric rubric = rubricRepositories.findById(rubricId).orElseThrow();
        // Charge le lieu seulement s'il a été sélectionné (locationId peut être null)
        Location location = locationId != null ? locationRepositories.findById(locationId).orElse(null) : null;
        itemService.addItem(rubric, title, description, startDate, endDate, location);
        return "redirect:/profiles/" + profileId;
    }

    /**
     * Modifie un élément existant.
     * URL : POST /items/{id}/edit
     * L'id est l'identifiant de l'item à modifier.
     */
    @PostMapping("/{id}/edit")
    public String editItem(
        @PathVariable UUID id,
        @RequestParam UUID profileId,
        @RequestParam String title,
        @RequestParam(required=false) String description,
        @RequestParam(required=false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam(required=false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
        @RequestParam(required=false) UUID locationId
    ) {
        Location location = locationId != null ? locationRepositories.findById(locationId).orElse(null) : null;
        itemService.updateItem(id, title, description, startDate, endDate, location);
        return "redirect:/profiles/" + profileId;
    }

    /**
     * Supprime définitivement un élément d'une rubrique.
     * URL : POST /items/{id}/delete
     */
    @PostMapping("/{id}/delete")
    public String deleteItem(@PathVariable UUID id, @RequestParam UUID profileId) {
        itemService.deleteItem(id);
        return "redirect:/profiles/" + profileId;
    }

    /**
     * Réorganise l'ordre des éléments dans une rubrique.
     * URL : POST /items/reorder
     * itemIds est une liste d'identifiants dans le nouvel ordre voulu.
     * Le premier item aura l'ordre 1, le deuxième l'ordre 2, etc.
     */
    @PostMapping("/reorder")
    public String reorder(@RequestParam List<UUID> itemIds, @RequestParam UUID profileId) {
        itemService.reorderItems(itemIds);
        return "redirect:/profiles/" + profileId;
    }
}
