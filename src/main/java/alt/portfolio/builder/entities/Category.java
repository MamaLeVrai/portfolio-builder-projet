package alt.portfolio.builder.entities;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Category {
	@Id
	private UUID id = UUID.randomUUID();
	
	@Column(length = 50, nullable = false)
	private String name;
	
	@Column(nullable = false)
	private boolean hasDates;
	
	@Column(nullable = false)
	private boolean hasLink;
}