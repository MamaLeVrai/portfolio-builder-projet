package alt.portfolio.builder.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Contrôleur pour la gestion des catégories.
 *
 * Une catégorie définit le "type" d'une rubrique :
 * par exemple "Expérience professionnelle", "Formation", "Projet", etc.
 * Pour l'instant ce contrôleur est vide car les catégories sont gérées
 * directement via la base de données (pas encore d'interface admin pour ça).
 *
 * Les routes commenceront par "/categories/" quand des fonctionnalités seront ajoutées.
 */
@RequestMapping("/categories")
@Controller
public class CategoryController {

}
