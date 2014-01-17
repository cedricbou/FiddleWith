package fiddle.config;

public class ResourceFileName {

	public static ResourceFileName VALUE = new ResourceFileName();
	
	public final static String name = "resources.conf";
		
	@Override
	public int hashCode() {
		return name.hashCode();
	}
	
	public boolean equals(Object obj) {
		if(obj instanceof ResourceFileName) {
			return true;
		}
		return false;
	};
	
	@Override
	public String toString() {
		return name;
	}
}
