package alt.portfolio.builder.entities;

import java.util.List;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Template {
	@Id
	private UUID id = UUID.randomUUID();
	
	@Column(length = 50, nullable = false)
	private String name;
	
	// description TEXT dans le MLD, on laisse Hibernate choisir le type sans columnDefinition
	@Column(nullable = true, length = 1000)
	private String description;
	
	// relation inverse avec Profile (template et template1)
	@OneToMany(mappedBy = "template")
	private List<Profile> profiles;
	
	@OneToMany(mappedBy = "template1")
	private List<Profile> profiles1;
}