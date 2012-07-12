
package nz.net.ultraq.web.thymeleaf;

import org.thymeleaf.Arguments;
import org.thymeleaf.context.VariablesMap;
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
	 * Return the list of page fragments to process in the decorator and content
	 * pages.
	 * 
	 * @param arguments
	 * @return List of page fragments for processing.
	 */
	@SuppressWarnings("unchecked")
	protected List<Node> getPageFragments(Arguments arguments) {

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
