package alt.portfolio.builder.dtos;

import alt.portfolio.builder.entities.Rubric;
import lombok.Data;

@Data
public class RubricRequestDto {
	private String name;
	private int order_;
	
	public Rubric toRubric(Rubric rubric) {
		rubric.setName(name);
		rubric.setOrder_(order_);
		return rubric;
	}
}
