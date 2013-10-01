package fiddle.xml;

public interface MaybeNode {

	public MaybeNode get(final String name);
	
	public String value();
	
	public MaybeNode[] list();
}
