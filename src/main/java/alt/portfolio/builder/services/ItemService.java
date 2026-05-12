package alt.portfolio.builder.services;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import alt.portfolio.builder.entities.Item;
import alt.portfolio.builder.repositories.ItemRepositories;

/**
 * ItemService — Le service qui gère les items (éléments d'une rubrique).
 *
 * Un "item" c'est par exemple une expérience professionnelle, un projet,
 * ou une compétence dans une rubrique. Ce service permet de les trouver
 * et de les modifier.
 *
 * Modifié dans Epic 5 (US-031) : ajout de la méthode pour associer
 * une image à un item (pour la vue Portfolio).
 */
@Service
public class ItemService {

    /** Le tiroir pour accéder aux items en base de données */
    @Autowired
    private ItemRepositories itemRepositories;

    /**
     * Cherche un item par son identifiant unique.
     * Si l'item n'existe pas, une exception est levée (le programme signale l'erreur).
     *
     * @param id L'identifiant unique de l'item à trouver
     * @return L'item trouvé
     */
    public Item getById(UUID id) {
        return itemRepositories.findById(id)
                .orElseThrow(() -> new RuntimeException("Item introuvable: " + id));
    }

    /**
     * (Epic 5 - US-031) Associe une image à un item de projet.
     * L'URL de l'image est sauvegardée dans la base de données.
     * Cette image s'affiche ensuite sur la carte du projet dans la vue Portfolio.
     *
     * @param itemId   L'identifiant de l'item auquel on ajoute une image
     * @param imageUrl L'URL de l'image (ex: "/uploads/items/mon-projet.jpg")
     * @return L'item mis à jour
     */
    public Item setItemImage(UUID itemId, String imageUrl) {
        Item item = getById(itemId);
        item.setImageUrl(imageUrl);
        return itemRepositories.save(item); // sauvegarde la modification en base
    }
}
