package domain;

public class Person {

	public final String first;
	public final String last;
	
	public Person(String first, String last) {
		this.first = first;
		this.last = last;
	}
	
	public String getName() {
		return this.first + " " + this.last;
	}
}
