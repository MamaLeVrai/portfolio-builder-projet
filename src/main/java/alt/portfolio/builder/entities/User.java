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
 * Entité représentant un utilisateur du système.
 * Implémente UserDetails pour l'intégration avec Spring Security.
 */
@Entity
@Getter
@Setter
public class User implements UserDetails {

	@Id
	private UUID id = UUID.randomUUID();

	/** Indique si l'utilisateur est archivé (soft delete) */
	private boolean archiver = false;

	@Column(length = 45, nullable = false)
	private String firstname = "";

	@Column(length = 45, nullable = false)
	private String lastname;

	@Column(length = 45, nullable = false, unique = true)
	private String username;

	@Column(length = 255)
	private String password;

	@Column(length = 45, nullable = false, unique = true)
	private String email;

	/** Rôle de l'utilisateur (USER, ADMIN, etc.) */
	@Column(length = 20)
	private String role = "USER";

	/** Liste des profils appartenant  cet utilisateur */
	@OneToMany(mappedBy = "owner", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Profile> profiles = new ArrayList<>();

	/**
	 * Ajoute un profil à cet utilisateur et établit la relation bidirectionnelle
	 */
	public void addProfile(Profile profile) {
		this.profiles.add(profile);
		profile.setOwner(this);
	}

	// ========== Implmentation de UserDetails pour Spring Security ==========

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
			if (this.role == null) {
					return new ArrayList<>();
			}
			return java.util.Collections.singletonList(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + this.role));
	}

	@Override
	public boolean isEnabled() {
		return !archiver;
	}
}
