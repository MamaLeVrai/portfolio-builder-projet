package alt.portfolio.builder.services;

import org.springframework.stereotype.Service;

/**
 * Service de base de données pour les lieux.
 *
 * Ce service est vide pour l'instant. Il est prévu pour contenir
 * les opérations de base de données sur les lieux (Location).
 *
 * Les lieux sont actuellement gérés directement via LocationRepositories
 * dans ItemController (on charge le lieu par son ID lors de l'ajout d'un item).
 */
@Service
public class DbLocationService {

}
