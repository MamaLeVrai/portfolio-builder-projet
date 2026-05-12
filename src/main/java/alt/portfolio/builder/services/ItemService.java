package alt.portfolio.builder.services;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import alt.portfolio.builder.entities.Item;
import alt.portfolio.builder.repositories.ItemRepositories;

@Service
public class ItemService {

	@Autowired
	private ItemRepositories itemRepositories;

	public Item getById(UUID id) {
		return itemRepositories.findById(id).orElseThrow(() -> new RuntimeException("Item introuvable: " + id));
	}

	// US-031: Associer une image à un item de projet
	public Item setItemImage(UUID itemId, String imageUrl) {
		Item item = getById(itemId);
		item.setImageUrl(imageUrl);
		return itemRepositories.save(item);
	}
}
