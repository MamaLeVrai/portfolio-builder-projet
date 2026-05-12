package alt.portfolio.builder.entities;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

/**
 * Entité qui représente un lieu géographique.
 *
 * Un lieu peut être associé à un item de rubrique pour indiquer où s'est passée
 * une expérience. Par exemple : nom = "Google", address = "Mountain View, Californie, USA".
 *
 * name    : le nom du lieu ou de l'établissement (ex : "École 42", "Mairie de Paris")
 * address : l'adresse complète ou la ville (ex : "75001 Paris, France")
 */
@Entity
@Getter @Setter
public class Location {

	/** Identifiant unique du lieu */
	@Id
	private UUID id = UUID.randomUUID();

	/** Nom du lieu ou de l'établissement (ex : "Université Lyon 1") */
	@Column(length = 120, nullable = false)
	private String name;

	/** Adresse ou localisation géographique (peut être null si non renseignée) */
	@Column(nullable = true, length = 1000)
	private String address;
}