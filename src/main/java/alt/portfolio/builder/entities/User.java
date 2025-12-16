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

@Entity
@Getter
@Setter
public class User implements UserDetails {

	@Id
	private UUID id = UUID.randomUUID();

	private boolean archiver = false;

	@Column(length = 45, nullable = false)
	private String firstname = "";

	@Column(length = 45, nullable = false)
	private String lastname;

	@Column(length = 45, nullable = false, unique = true)
	private String username;

	@Column(length = 255, nullable = true)
	private String password;

	@Column(length = 45, nullable = false, unique = true)
	private String email;

	@OneToMany(mappedBy = "owner", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Profile> profiles;

	public void addProdile(Profile profile) {
		this.profiles.add(profile);
		profile.setOwner(this);

	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return new ArrayList<GrantedAuthority>();
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public boolean isEnabled() {
		return !archiver;
	}

}
