package alt.portfolio.builder.entities;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.Getter;
import lombok.Setter;

/**
 * Entité qui représente une image stockée dans le système.
 *
 * Une image a une URL (son adresse web) et peut être associée à plusieurs profils.
 * La relation est "plusieurs profils ↔ plusieurs images" (ManyToMany).
 *
 * Note : dans l'Epic 5, on a aussi ajouté imageUrl directement sur Profile et Item
 * pour les photos de profil et images de projets uploadées localement.
 * Cette entité Image représente le système d'images "historique" par URL.
 */
@Entity
@Getter @Setter
public class Image {

	/** Identifiant unique de l'image */
	@Id
	private UUID id = UUID.randomUUID();

	/** Adresse web de l'image (ex : "https://exemple.com/photo.jpg") */
	@Column(length = 255, nullable = false)
	private String url;

	/**
	 * Liste des profils qui utilisent cette image.
	 * mappedBy = "images" : la relation est gérée du côté de Profile (champ "images").
	 */
	@ManyToMany(mappedBy = "images")
	private java.util.List<Profile> profiles;
}