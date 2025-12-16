package alt.portfolio.builder.entities;

import java.util.List;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Profile {
	@Id
	private UUID id = UUID.randomUUID();
	
	@Column(length = 150, nullable = false)
	private String name;
	
	@ManyToOne(optional = true)
	private Template template;
	
	@ManyToOne(optional = true)
	private Template template1;
	
	// #idOwner : propriétaire du profil
	@ManyToOne(optional = false)
	private User owner;
	
	// profile_Image (#Id_Profile, #id)
	@ManyToMany
	@JoinTable(name = "profile_image",
			joinColumns = @JoinColumn(name = "profile_id"),
			inverseJoinColumns = @JoinColumn(name = "image_id"))
	private List<Image> images;
	
	// relation inverse avec Rubric
	@OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Rubric> rubrics;
}