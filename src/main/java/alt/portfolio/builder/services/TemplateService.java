package alt.portfolio.builder.services;

import org.springframework.stereotype.Service;

/**
 * Service de haut niveau pour la gestion des templates.
 *
 * Ce service est vide pour l'instant. La logique des templates est gérée dans :
 * - TemplateInitializer : crée les 4 templates de base au démarrage du serveur
 * - ProfileService : contient setTemplate() pour appliquer un template à un profil
 *
 * Ce service pourrait accueillir des méthodes pour gérer les templates
 * si de nouveaux types de templates sont ajoutés dans le futur.
 */
@Service
public class TemplateService {

}
