package alt.portfolio.builder.entities;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Item {
	@Id
	private UUID id = UUID.randomUUID();
	
	@Column(length = 150, nullable = false)
	private String title;
	
	@Column(nullable = true, length = 1000)
	private String description;
	
	private LocalDate startDate;
	
	private LocalDate endDate;
	
	// order_ BYTE dans le MLD
	@Column(name = "order_", nullable = false)
	private Byte order;
	
	// #Id_Location*
	@ManyToOne(optional = true)
	private Location location;
	
	// #Id_Rubric
	@ManyToOne(optional = false)
	private Rubric rubric;
}