package alt.portfolio.builder.entities;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Profile {

	@Id
	private UUID id = UUID.randomUUID();

	private boolean archived = false;

	@Column(length = 150, nullable = false)
	private String name;

	@Column(length = 500, nullable = false)
	private String description = "";

	@Column(length = 255, nullable = true)
	private String imageUrl;

	@Column(nullable = false, updatable = false)
	@CreationTimestamp
	private LocalDateTime createdAt;

	@Column(nullable = false)
	@UpdateTimestamp
	private LocalDateTime updatedAt;

	@Column(length = 20, nullable = false)
	private String status = "draft"; // draft, published, archived

	@Column(nullable = false)
	private boolean isDefault = false;

	@ManyToOne(optional = true)
	private Template template;

	@ManyToOne(optional = true)
	private Template template1;

	// #idOwner : propriétaire du profil
	@ManyToOne(optional = false)
	private User owner;

	// profile_Image (#Id_Profile, #id)
	@ManyToMany
	@JoinTable(name = "profile_image", joinColumns = @JoinColumn(name = "profile_id"), inverseJoinColumns = @JoinColumn(name = "image_id"))
	private List<Image> images;

	// relation inverse avec Rubric
	@OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Rubric> rubrics;
}