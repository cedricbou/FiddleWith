package domain;

public class User {
	
	public final String name;
	public final String[] tags;
	
	public User(final String name, final String... tags) {
		this.name = name;
		this.tags = tags;
	}

}
