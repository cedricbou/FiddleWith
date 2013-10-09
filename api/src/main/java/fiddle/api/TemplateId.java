package fiddle.api;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.NotBlank;

public class TemplateId {

	@NotNull
	@NotBlank
	@Pattern(regexp = "^\\w+$")
	public final String id;
	
	public static final TemplateId NONE = new TemplateId("__NONE__");
	
	public TemplateId(final String id) {
		this.id = id;
	}
	
	public String toString() {
		return id;
	}
	
	@Override
	public int hashCode() {
		return (44 + id).hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof TemplateId && this.hashCode() == obj.hashCode();
	}
}
