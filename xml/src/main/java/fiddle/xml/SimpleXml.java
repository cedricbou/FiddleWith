package fiddle.xml;

import java.io.ByteArrayInputStream;

import org.apache.axiom.om.OMXMLBuilderFactory;

import com.google.common.collect.ImmutableMap;

public class SimpleXml implements MaybeNode {

	private final ElementNode root;
	
	/*
	 * Map containing prefixes and associated namespace.
	 */
	private final ImmutableMap<String, String> ns;

	public SimpleXml(final String content) {
		this.root = buildRoot(content);
		this.ns = ImmutableMap.<String, String>of();
	}
	
	private SimpleXml(final ElementNode root, final ImmutableMap<String, String> ns) {
		this.root = root.withNS(ns);
		this.ns = ns;
	}

	private ElementNode buildRoot(final String content) {
		return (ElementNode)ElementNode.some(OMXMLBuilderFactory.createOMBuilder(
				new ByteArrayInputStream(content.getBytes()))
				.getDocumentElement());
	}

	public SimpleXml withNS(final String namespace, final String prefix) {
		// FIXME : find a way to do it a la scala, with an immutable map : ns + (namespace -> prefix) with constant time insertion.
		return new SimpleXml(root, ImmutableMap.<String, String>builder().putAll(ns).put(prefix, namespace).build());
	}
	
	@Override
	public MaybeNode get(String name) {
		return root.get(name);
	}
	
	@Override
	public String value() {
		return root.value();
	}

	@Override
	public MaybeNode[] list() {
		return root.list();
	}

	@Override
	public String toString() {
		return root.toString();
	}
}
