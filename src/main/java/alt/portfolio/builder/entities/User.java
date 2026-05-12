package alt.portfolio.builder.entities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

/**
 * Entité qui représente un utilisateur de l'application.
 *
 * Chaque ligne dans la table "user" de la base de données correspond à un objet User.
 * Cette classe implémente UserDetails, ce qui permet à Spring Security de gérer
 * automatiquement la connexion et les droits d'accès.
 *
 * @Entity : dit à JPA que cette classe est une table en base de données
 * @Getter @Setter : Lombok génère automatiquement tous les getters et setters
 */
@Entity
@Getter
@Setter
public class User implements UserDetails {

	/** Identifiant unique généré automatiquement (un grand nombre aléatoire unique) */
	@Id
	private UUID id = UUID.randomUUID();

	/**
	 * Soft delete : au lieu de supprimer l'utilisateur, on le "cache".
	 * archiver = true signifie que l'utilisateur est désactivé.
	 * archiver = false (par défaut) signifie qu'il est actif.
	 */
	private boolean archiver = false;

	/** Prénom de l'utilisateur (max 45 caractères, obligatoire) */
	@Column(length = 45, nullable = false)
	private String firstname = "";

	/** Nom de famille (max 45 caractères, obligatoire) */
	@Column(length = 45, nullable = false)
	private String lastname;

	/** Nom d'utilisateur unique pour se connecter (max 45 caractères) */
	@Column(length = 45, nullable = false, unique = true)
	private String username;

	/** Mot de passe haché (jamais stocké en clair !) */
	@Column(length = 255)
	private String password;

	/** Adresse email unique (sert aussi à se connecter) */
	@Column(length = 45, nullable = false, unique = true)
	private String email;

	/**
	 * Rôle de l'utilisateur : "USER" pour un utilisateur normal, "ADMIN" pour un administrateur.
	 * Les admins peuvent gérer tous les utilisateurs.
	 */
	@Column(length = 20)
	private String role = "USER";

	/**
	 * Liste de tous les profils créés par cet utilisateur.
	 * mappedBy = "owner" : la relation est gérée du côté de Profile (champ "owner").
	 * CascadeType.ALL : si on supprime un User, tous ses profils sont supprimés aussi.
	 * FetchType.LAZY : les profils ne sont chargés depuis la BDD que quand on en a besoin.
	 */
	@OneToMany(mappedBy = "owner", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Profile> profiles = new ArrayList<>();

	/**
	 * Ajoute un profil à cet utilisateur et fait le lien dans les deux sens :
	 * - le profil est ajouté à la liste de l'utilisateur
	 * - le profil connaît son propriétaire (profile.owner = this)
	 */
	public void addProfile(Profile profile) {
		this.profiles.add(profile);
		profile.setOwner(this);
	}

	// ========== Méthodes imposées par UserDetails (Spring Security) ==========

	/**
	 * Retourne les droits de l'utilisateur.
	 * Spring Security a besoin de "ROLE_USER" ou "ROLE_ADMIN" (avec le préfixe ROLE_).
	 * On construit cette chaîne en ajoutant "ROLE_" devant le rôle stocké en base.
	 */
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		if (this.role == null) {
			return new ArrayList<>();
		}
		return java.util.Collections.singletonList(
			new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + this.role)
		);
	}

	/**
	 * Un utilisateur archivé est considéré comme désactivé.
	 * Spring Security bloquera automatiquement sa connexion.
	 */
	@Override
	public boolean isEnabled() {
		return !archiver;
	}
}
