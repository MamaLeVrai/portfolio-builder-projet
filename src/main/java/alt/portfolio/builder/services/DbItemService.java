package alt.portfolio.builder.services;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import alt.portfolio.builder.repositories.ItemRepositories;
import alt.portfolio.builder.entities.Item;
import alt.portfolio.builder.entities.Rubric;
import alt.portfolio.builder.entities.Location;
import java.util.UUID;
import java.util.List;
import java.util.Optional;
import java.time.LocalDate;

/**
 * Service de base de données pour gérer les items (éléments des rubriques).
 *
 * Un item est une entrée concrète dans une rubrique, comme :
 * - "Développeur web chez Google, 2023-2024" dans la rubrique "Expériences"
 * - "Licence Informatique, Paris 2021-2024" dans la rubrique "Formation"
 *
 * Ce service s'occupe de créer, modifier, supprimer et réordonner les items
 * directement dans la base de données.
 */
@Service
public class DbItemService {

    @Autowired
    private ItemRepositories itemRepositories;

    /**
     * Crée un nouvel item et l'ajoute à une rubrique.
     *
     * L'ordre de l'item est calculé automatiquement : si la rubrique a déjà 3 items,
     * le nouvel item aura l'ordre 4 (= 3 + 1).
     *
     * @param rubric    La rubrique à laquelle appartient cet item
     * @param title     Le titre de l'item (obligatoire)
     * @param description Texte descriptif (optionnel)
     * @param startDate Date de début (optionnelle)
     * @param endDate   Date de fin (optionnelle)
     * @param location  Lieu associé (optionnel)
     * @return L'item créé et sauvegardé en base de données
     */
    public Item addItem(Rubric rubric, String title, String description, LocalDate startDate, LocalDate endDate, Location location) {
        Item item = new Item();
        item.setRubric(rubric);
        item.setTitle(title);
        item.setDescription(description);
        item.setStartDate(startDate);
        item.setEndDate(endDate);
        item.setLocation(location);

        // Calcule la position de ce nouvel item dans la rubrique
        if (rubric.getItems() != null && !rubric.getItems().isEmpty()) {
            item.setOrder((byte)(rubric.getItems().size() + 1));
        } else {
            item.setOrder((byte)1); // Premier item de la rubrique
        }
        return itemRepositories.save(item);
    }

    /**
     * Met à jour un item existant avec de nouvelles valeurs.
     * On retrouve l'item par son ID, on modifie ses champs, puis on sauvegarde.
     */
    public void updateItem(UUID id, String title, String description, LocalDate startDate, LocalDate endDate, Location location) {
        Optional<Item> opt = itemRepositories.findById(id);
        if (opt.isPresent()) {
            Item item = opt.get();
            item.setTitle(title);
            item.setDescription(description);
            item.setStartDate(startDate);
            item.setEndDate(endDate);
            item.setLocation(location);
            itemRepositories.save(item);
        }
    }

    /**
     * Supprime définitivement un item de la base de données.
     * @param id L'identifiant de l'item à supprimer
     */
    public void deleteItem(UUID id) {
        itemRepositories.deleteById(id);
    }

    /**
     * Réorganise les items dans une rubrique selon un nouvel ordre.
     *
     * orderedIds est une liste d'IDs dans l'ordre souhaité.
     * On parcourt cette liste et on attribue l'ordre 1, 2, 3... à chaque item.
     * Exemple : si orderedIds = [C, A, B], alors C aura l'ordre 1, A l'ordre 2, B l'ordre 3.
     *
     * @param orderedIds Liste des IDs des items dans le nouvel ordre voulu
     */
    public void reorderItems(List<UUID> orderedIds) {
        for (int i = 0; i < orderedIds.size(); i++) {
            Optional<Item> opt = itemRepositories.findById(orderedIds.get(i));
            if (opt.isPresent()) {
                Item item = opt.get();
                item.setOrder((byte)(i + 1)); // i+1 car on commence à 1, pas à 0
                itemRepositories.save(item);
            }
        }
    }
}
