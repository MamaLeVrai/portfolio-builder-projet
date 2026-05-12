package alt.portfolio.builder.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import alt.portfolio.builder.entities.Template;
import alt.portfolio.builder.repositories.TemplateRepositories;

/**
 * TemplateInitializer — Crée les templates de base au démarrage de l'application.
 *
 * C'est comme un employé qui arrive le premier le matin et qui prépare la salle
 * avant que les clients arrivent. Au démarrage, il vérifie si des templates existent
 * déjà en base de données. Si non, il en crée 4 pour que l'utilisateur ait
 * directement des choix de mise en page.
 *
 * "CommandLineRunner" = Spring appelle automatiquement la méthode "run()"
 * une fois que toute l'application est démarrée.
 *
 * Créé dans Epic 5 (US-024).
 */
@Component
public class TemplateInitializer implements CommandLineRunner {

    /** Le tiroir pour accéder aux templates en base de données */
    @Autowired
    private TemplateRepositories templateRepositories;

    /**
     * Méthode appelée automatiquement au démarrage de l'application.
     * Si la table des templates est vide (count() == 0), on crée les 4 templates de base.
     * Le "if" évite de recréer des doublons à chaque redémarrage.
     */
    @Override
    public void run(String... args) {
        if (templateRepositories.count() == 0) {
            createTemplate("Classique", "Mise en page sobre et professionnelle", "classic");
            createTemplate("Moderne", "Design épuré avec accent coloré", "modern");
            createTemplate("Minimal", "Ultra-minimaliste, contenu au premier plan", "minimal");
            createTemplate("Créatif", "Mise en page dynamique pour les créatifs", "creative");
        }
    }

    /**
     * Crée et sauvegarde un nouveau template dans la base de données.
     *
     * @param name        Le nom affiché à l'utilisateur (ex: "Classique")
     * @param description Une courte phrase décrivant le style
     * @param layoutKey   La clé CSS du layout (ex: "classic" → classe CSS ".layout-classic")
     */
    private void createTemplate(String name, String description, String layoutKey) {
        Template t = new Template();
        t.setName(name);
        t.setDescription(description);
        t.setLayoutKey(layoutKey);
        templateRepositories.save(t); // sauvegarde en base de données
    }
}
