package alt.portfolio.builder.entities;

import java.util.List;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Rubric {
	@Id
	private UUID id = UUID.randomUUID();
	
	
	@Column(length = 120, nullable = false)
	private String name;
	
	
	// order_ BYTE dans le MLD
	@Column(name = "order_", nullable = false)
	private Byte order;
	
	// #Id_Category
	@ManyToOne(optional = false)
	private Category category;
	
	// #Id_Profile
	@ManyToOne(optional = false)
	private Profile profile;
	
	// relation inverse vers Item
	@OneToMany(mappedBy = "rubric", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Item> items;
}