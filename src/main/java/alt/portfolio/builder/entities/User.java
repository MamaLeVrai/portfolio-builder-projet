package alt.portfolio.builder.entities;

import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class User {
	
	@Id
	private UUID id = UUID.randomUUID();
	
	@Column(length = 45, nullable = false)
	private String firstname = "";

	@Column(length = 45, nullable = false)
	private String lastname;

	@Column(length = 45, nullable = false, unique = true)
	private String username;

	@Column(length = 45, nullable = true)
	private String password;
	
	@Column(length = 45, nullable = false, unique = true)
	private String email;
	
	@OneToMany(mappedBy = "owner", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true )
	private List<Profile> profiles;

	public void addProdile(Profile profile) {
		this.profiles.add(profile);
		profile.setOwner(this);
		
	}
}
