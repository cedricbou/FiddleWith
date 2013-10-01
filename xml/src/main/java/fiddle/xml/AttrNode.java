package fiddle.xml;

public class AttrNode implements MaybeNode {

	private final String value;
	
	private final static MaybeNode[] NONE = new MaybeNode[] {};
	
	protected AttrNode(final String value) {
		this.value = value;
	}
	
	@Override
	public MaybeNode get(String name) {
		return ElementNode.UNDEF;
	}
	
	@Override
	public MaybeNode[] list() {
		return NONE;
	}
	
	@Override
	public String value() {
		return value;
	}
	
	@Override
	public String toString() {
		return value();
	}
	
}
