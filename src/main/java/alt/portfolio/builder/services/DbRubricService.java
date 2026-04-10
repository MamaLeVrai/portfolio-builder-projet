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

@Service
public class DbRubricService {

    @Autowired
    private RubricRepositories rubricRepositories;

    public Rubric addRubric(Profile profile, Category category, String name) {
        Rubric r = new Rubric();
        r.setProfile(profile);
        r.setCategory(category);
        r.setName(name);
        // Find max order
        Byte order = 0;
        if (profile.getRubrics() != null && !profile.getRubrics().isEmpty()) {
            r.setOrder((byte)(profile.getRubrics().size() + 1));
        } else {
            r.setOrder((byte)1);
        }
        return rubricRepositories.save(r);
    }

    public void updateRubric(UUID id, String newName) {
        Optional<Rubric> opt = rubricRepositories.findById(id);
        if (opt.isPresent()) {
            Rubric r = opt.get();
            r.setName(newName);
            rubricRepositories.save(r);
        }
    }

    public void deleteRubric(UUID id) {
        rubricRepositories.deleteById(id);
    }

    public void toggleVisibility(UUID id) {
        Optional<Rubric> opt = rubricRepositories.findById(id);
        if (opt.isPresent()) {
            Rubric r = opt.get();
            r.setVisible(!r.isVisible());
            rubricRepositories.save(r);
        }
    }

    public void reorderRubrics(List<UUID> orderedIds) {
        for (int i = 0; i < orderedIds.size(); i++) {
            Optional<Rubric> opt = rubricRepositories.findById(orderedIds.get(i));
            if (opt.isPresent()) {
                Rubric r = opt.get();
                r.setOrder((byte)(i + 1));
                rubricRepositories.save(r);
            }
        }
    }
}
