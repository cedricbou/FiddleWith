package fiddle.api;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.NotBlank;

public class WorkspaceId {

	public static WorkspaceId COMMON = new WorkspaceId("@common");
	
	@NotNull
	@NotBlank
	@Pattern(regexp = "^\\@{0,1}\\w+$")
	public final String id;

	public WorkspaceId(final String id) {
		this.id = id;
	}

	public String toString() {
		return id;
	}

	@Override
	public int hashCode() {
		return (99 + id).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof WorkspaceId && this.hashCode() == obj.hashCode();
	}

}
