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

@RequestMapping("/items")
@Controller
public class ItemController {

    @Autowired
    private DbItemService itemService;

    @Autowired
    private RubricRepositories rubricRepositories;

    @Autowired
    private LocationRepositories locationRepositories;

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
        Rubric rubric = rubricRepositories.findById(rubricId).orElseThrow();
        Location location = locationId != null ? locationRepositories.findById(locationId).orElse(null) : null;
        itemService.addItem(rubric, title, description, startDate, endDate, location);
        return "redirect:/profiles/" + profileId;
    }

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

    @PostMapping("/{id}/delete")
    public String deleteItem(@PathVariable UUID id, @RequestParam UUID profileId) {
        itemService.deleteItem(id);
        return "redirect:/profiles/" + profileId;
    }

    @PostMapping("/reorder")
    public String reorder(@RequestParam List<UUID> itemIds, @RequestParam UUID profileId) {
        itemService.reorderItems(itemIds);
        return "redirect:/profiles/" + profileId;
    }
}
