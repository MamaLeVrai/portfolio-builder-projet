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

@RequestMapping("/rubrics")
@Controller
public class RubricController {

    @Autowired
    private DbRubricService rubricService;
    
    @Autowired
    private ProfileRepositories profileRepositories;

    @Autowired
    private CategoryRepositories categoryRepositories;

    @PostMapping("/add")
    public String addRubric(@RequestParam UUID profileId, @RequestParam UUID categoryId, @RequestParam String name) {
        Profile p = profileRepositories.findById(profileId).orElseThrow();
        Category c = categoryRepositories.findById(categoryId).orElseThrow();
        rubricService.addRubric(p, c, name);
        return "redirect:/profiles/" + profileId;
    }

    @PostMapping("/{id}/edit")
    public String editRubric(@PathVariable UUID id, @RequestParam String name, @RequestParam UUID profileId) {
        rubricService.updateRubric(id, name);
        return "redirect:/profiles/" + profileId;
    }

    @PostMapping("/{id}/delete")
    public String deleteRubric(@PathVariable UUID id, @RequestParam UUID profileId) {
        rubricService.deleteRubric(id);
        return "redirect:/profiles/" + profileId;
    }

    @PostMapping("/{id}/toggle-visibility")
    public String toggleVisibility(@PathVariable UUID id, @RequestParam UUID profileId) {
        rubricService.toggleVisibility(id);
        return "redirect:/profiles/" + profileId;
    }

    @PostMapping("/reorder")
    public String reorder(@RequestParam List<UUID> rubricIds, @RequestParam UUID profileId) {
        rubricService.reorderRubrics(rubricIds);
        return "redirect:/profiles/" + profileId;
    }
}
