package alt.portfolio.builder.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import alt.portfolio.builder.dtos.RubricRequestDto;
import alt.portfolio.builder.entities.Rubric;
import alt.portfolio.builder.repositories.RubricRepositories;

@Service
public class RubricService {

	@Autowired
	private RubricRepositories rubricRepositories;
	
	@Autowired
	private DbRubricService dbRubricServices;
	
	public List<Rubric> getAllRubrics() {
		return rubricRepositories.findAll();
	}
	
	public Rubric createRubric(RubricRequestDto rubricRequest) {
		// vérification : nom déjà utilisé ?
		rubricRepositories.findByName(rubricRequest.getName())
			.ifPresent(r -> { throw new IllegalArgumentException("Nom de rubrique déjà utilisé"); });
		
		Rubric rubric = rubricRequest.toRubric(new Rubric());
		return rubricRepositories.save(rubric);
	}
	
	public Rubric getRubricById(java.util.UUID id) {
		return rubricRepositories.findById(id)
			.orElseThrow(() -> new RuntimeException("Rubrique introuvable: " + id));
	}
}
