package fiddle.xml;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMElement;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.collect.Iterators;

public class ElementNode implements MaybeNode {

	protected final static ElementNode UNDEF = new ElementNode();

	private final static MaybeNode[] NONE = new MaybeNode[] {};

	private final Map<String, String> ns;

	@SuppressWarnings("unchecked")
	protected static MaybeNode some(final OMElement element) {
		return new ElementNode(Iterators.forArray(element),
				Collections.EMPTY_MAP);
	}

	protected static MaybeNode some(final OMElement element,
			Map<String, String> ns) {
		return new ElementNode(Iterators.forArray(element), ns);
	}

	@SuppressWarnings("unchecked")
	protected static MaybeNode some(final Iterator<OMElement> elements) {
		return new ElementNode(elements, Collections.EMPTY_MAP);
	}

	protected static MaybeNode some(final Iterator<OMElement> elements,
			Map<String, String> ns) {
		return new ElementNode(elements, ns);
	}

	protected static MaybeNode some(final String value) {
		return new AttrNode(value);
	}

	private final Optional<Iterator<OMElement>> el;

	private Optional<OMElement> first = Optional.absent();

	private Optional<OMElement[]> all = Optional.absent();

	private Optional<OMElement> first() {
		if (first.isPresent()) {
			return first;
		}

		if (el.isPresent() && el.get().hasNext()) {
			final OMElement ele = el.get().next();
			first = Optional.of(ele);
			return first;
		} else {
			return Optional.absent();
		}
	}

	private Optional<OMElement[]> all() {
		if (all.isPresent()) {
			return all;
		}

		if (el.isPresent()) {
			final OMElement[] allEl = Iterators.toArray(el.get(),
					OMElement.class);

			// If first was already read, then we should take it into account
			// when building the all array.
			if (!first.isPresent()) {
				if (allEl.length > 0) {
					first = Optional.of(allEl[0]);
				}
				all = Optional.of(allEl);
			} else {
				final OMElement[] firstAndAllEl = new OMElement[allEl.length + 1];
				firstAndAllEl[0] = first.get();
				System.arraycopy(allEl, 0, firstAndAllEl, 1, allEl.length);
			}

			return all;
		} else {
			return Optional.absent();
		}
	}

	protected ElementNode withNS(final Map<String, String> ns) {
		// FIXME : this is suboptimal, because it needs to parse elements to
		// create a copy of the iterator... I should refactor this with a
		// mutable namespace registry to avoid all those issues.
		if (all().isPresent()) {
			return new ElementNode(Iterators.forArray(all().get()), ns);
		} else {
			return UNDEF;
		}
	}

	/**
	 * Creates a defined node with a map of namespaces/prefix if element is not
	 * null.
	 */
	private ElementNode(Iterator<OMElement> el, Map<String, String> ns) {
		this.el = Optional.fromNullable(el);
		this.ns = ns;
	}

	/**
	 * An undef node (no element defined).
	 */
	private ElementNode() {
		this.el = Optional.absent();
		this.ns = new HashMap<String, String>();
	}

	@SuppressWarnings("unchecked")
	@Override
	public MaybeNode get(String name) {
		final QName qn = stringtoQName(name);

		if (el.isPresent() && first().isPresent()) {
			final String attr = first().get().getAttributeValue(qn);
			if (attr == null) {
				return some(first().get().getChildrenWithName(qn), ns);
			} else {
				return some(attr);
			}
		} else {
			return UNDEF;
		}
	}

	private final QName stringtoQName(final String name) {
		final String[] split = name.split("\\:");

		if (split.length <= 1) {
			return new QName(split[0]);
		} else {
			final String namespace = ns.get(split[0]);

			if (namespace != null) {
				return new QName(namespace, split[1]);
			} else {
				return new QName(split[1]);
			}
		}
	}

	@Override
	public MaybeNode[] list() {
		if (all().isPresent()) {
			final Iterator<MaybeNode> nodes = Iterators.transform(
					Iterators.forArray(all().get()),
					new Function<OMElement, MaybeNode>() {
						@Override
						public MaybeNode apply(OMElement input) {
							return some(input, ns);
						}
					});

			return Iterators.toArray(nodes, MaybeNode.class);
		} else {
			return NONE;
		}
	}

	@Override
	public String value() {
		if (all().isPresent()) {
			Iterator<String> texted = Iterators.transform(
					Iterators.forArray(all().get()),
					new Function<OMElement, String>() {
						@Override
						public String apply(OMElement input) {
							if (input != null) {
								if (input.getFirstElement() != null) {
									return input.toString();
								} else {
									return input.getText();
								}
							} else {
								return "";
							}
						}
					});

			return Joiner.on("").join(texted);
		} else {
			return "undef";
		}
	}

	@Override
	public String toString() {
		return value();
	}
}
