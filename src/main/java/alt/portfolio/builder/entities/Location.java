package alt.portfolio.builder.entities;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Location {
	@Id
	private UUID id = UUID.randomUUID();
	
	@Column(length = 120, nullable = false)
	private String name;
	
	@Column(nullable = true, length = 1000)
	private String address;
}