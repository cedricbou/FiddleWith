package fiddle.api;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.NotBlank;

public class FiddleId {

	@NotNull
	@NotBlank
	@Pattern(regexp = "^\\w+$")
	public final String id;
	
	public FiddleId(final String id) {
		this.id = id;
	}
	
	public String toString() {
		return id;
	}
	
	@Override
	public int hashCode() {
		return (37 + id).hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof FiddleId && this.hashCode() == obj.hashCode();
	}
}
