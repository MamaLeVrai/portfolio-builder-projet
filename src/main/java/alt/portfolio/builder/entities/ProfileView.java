package alt.portfolio.builder.entities;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class ProfileView {
	@Id
	private UUID id = UUID.randomUUID();

	@ManyToOne(optional = false)
	private Profile profile;

	@Column(length = 10, nullable = false)
	private String viewType; // "cv" or "portfolio"

	@Column(length = 45)
	private String visitorIp;

	@CreationTimestamp
	private LocalDateTime viewedAt;
}
