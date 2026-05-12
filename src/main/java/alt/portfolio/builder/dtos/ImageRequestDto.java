package alt.portfolio.builder.dtos;

/**
 * DTO (Data Transfer Object) pour les requêtes de gestion des images.
 *
 * Ce DTO est vide pour l'instant. L'upload d'images est géré différemment :
 * - Photos de profil et images de projets → via MultipartFile dans CustomizationController
 * - Images par URL → via le champ imageUrl dans les entités Profile et Item
 *
 * Ce DTO pourrait être utilisé si une interface de gestion des images est ajoutée.
 */
public class ImageRequestDto {

}
