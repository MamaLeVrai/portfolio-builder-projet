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

@Service
public class DbItemService {

    @Autowired
    private ItemRepositories itemRepositories;

    public Item addItem(Rubric rubric, String title, String description, LocalDate startDate, LocalDate endDate, Location location) {
        Item item = new Item();
        item.setRubric(rubric);
        item.setTitle(title);
        item.setDescription(description);
        item.setStartDate(startDate);
        item.setEndDate(endDate);
        item.setLocation(location);
        
        Byte order = 0;
        if (rubric.getItems() != null && !rubric.getItems().isEmpty()) {
            item.setOrder((byte)(rubric.getItems().size() + 1));
        } else {
            item.setOrder((byte)1);
        }
        return itemRepositories.save(item);
    }

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

    public void deleteItem(UUID id) {
        itemRepositories.deleteById(id);
    }

    public void reorderItems(List<UUID> orderedIds) {
        for (int i = 0; i < orderedIds.size(); i++) {
            Optional<Item> opt = itemRepositories.findById(orderedIds.get(i));
            if (opt.isPresent()) {
                Item item = opt.get();
                item.setOrder((byte)(i + 1));
                itemRepositories.save(item);
            }
        }
    }
}
