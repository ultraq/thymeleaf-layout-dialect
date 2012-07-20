
package nz.net.ultraq.web.thymeleaf;

import static nz.net.ultraq.web.thymeleaf.FragmentProcessor.ATTRIBUTE_NAME_FRAGMENT;
import static nz.net.ultraq.web.thymeleaf.LayoutDialect.LAYOUT_PREFIX;

import org.thymeleaf.Arguments;
import org.thymeleaf.context.VariablesMap;
import org.thymeleaf.dom.Element;
import org.thymeleaf.dom.Node;
import org.thymeleaf.processor.attr.AbstractAttrProcessor;

import java.util.ArrayList;
import java.util.List;

/**
 * Common code for the layout processors.
 * 
 * @author Emanuel Rabina
 */
public abstract class AbstractProcessor extends AbstractAttrProcessor {

	private static final String CONTEXT_VAR_FRAGMENTS = "fragments";

	/**
	 * Subclass constructor, set the attribute name that this processor will
	 * respond to.
	 * 
	 * @param attribute
	 */
	protected AbstractProcessor(String attribute) {

		super(attribute);
	}

	/**
	 * Recursive search for all <tt>layout:fragment</tt> elements.
	 * 
	 * @param fragments List of all fragments found.
	 * @param element	Node to initiate the search from.
	 */
	protected void findFragments(List<Node> fragments, Element element) {

		for (Element child: element.getElementChildren()) {
			if (child.hasAttribute(LAYOUT_PREFIX + ":" + ATTRIBUTE_NAME_FRAGMENT)) {
				fragments.add(child);
			}
			if (child.hasChildren()) {
				findFragments(fragments, child);
			}
		}
	}

	/**
	 * Return the list of layout fragments present in content, decorator, and
	 * included pages.
	 * 
	 * @param arguments
	 * @return List of page fragments for processing.
	 */
	@SuppressWarnings("unchecked")
	protected List<Node> getFragmentList(Arguments arguments) {

		VariablesMap<String,Object> variables = arguments.getContext().getVariables();
		List<Node> pagefragments = (List<Node>)variables.get(CONTEXT_VAR_FRAGMENTS);
		if (pagefragments == null) {
			pagefragments = new ArrayList<Node>();
			variables.put(CONTEXT_VAR_FRAGMENTS, pagefragments);
		}
		return pagefragments;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int getPrecedence() {

		return 0;
	}
}
