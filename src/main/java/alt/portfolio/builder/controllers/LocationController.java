package alt.portfolio.builder.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Contrôleur pour la gestion des lieux.
 *
 * Un lieu (Location) peut être associé à un item de rubrique :
 * par exemple "Paris, France" pour un stage, ou "Lyon, Campus" pour une école.
 * Ce contrôleur est vide pour l'instant, les lieux étant gérés directement
 * dans les formulaires d'ajout d'items.
 *
 * Les routes commenceront par "/locations/" quand des fonctionnalités seront ajoutées.
 */
@RequestMapping("/locations")
@Controller
public class LocationController {

}
