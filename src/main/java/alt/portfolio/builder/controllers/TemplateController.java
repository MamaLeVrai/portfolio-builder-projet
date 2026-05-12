package alt.portfolio.builder.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Contrôleur pour la gestion des templates visuels.
 *
 * Un template définit la mise en page du CV ou portfolio :
 * classic, modern, minimal ou creative.
 * Ce contrôleur est vide car les templates sont créés automatiquement
 * au démarrage du serveur par TemplateInitializer et sont sélectionnables
 * via CustomizationController.
 *
 * Les routes commenceront par "/templates/" si une interface d'administration
 * des templates est ajoutée dans le futur.
 */
@RequestMapping("/templates")
@Controller
public class TemplateController {

}
