package alt.portfolio.builder.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import alt.portfolio.builder.entities.Template;
import alt.portfolio.builder.repositories.TemplateRepositories;

@Component
public class TemplateInitializer implements CommandLineRunner {

	@Autowired
	private TemplateRepositories templateRepositories;

	@Override
	public void run(String... args) {
		if (templateRepositories.count() == 0) {
			createTemplate("Classique", "Mise en page sobre et professionnelle", "classic");
			createTemplate("Moderne", "Design épuré avec accent coloré", "modern");
			createTemplate("Minimal", "Ultra-minimaliste, contenu au premier plan", "minimal");
			createTemplate("Créatif", "Mise en page dynamique pour les créatifs", "creative");
		}
	}

	private void createTemplate(String name, String description, String layoutKey) {
		Template t = new Template();
		t.setName(name);
		t.setDescription(description);
		t.setLayoutKey(layoutKey);
		templateRepositories.save(t);
	}
}
