
package nz.net.ultraq.web.thymeleaf;

import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Attribute;
import org.thymeleaf.dom.Element;
import org.thymeleaf.dom.Node;
import org.thymeleaf.processor.ProcessorResult;
import org.thymeleaf.standard.expression.FragmentSelection;
import org.thymeleaf.standard.expression.StandardExpressionProcessor;

import java.util.List;

/**
 * Processor for the 'template:fragment' attribute, includes or replaces the
 * content of the fragment into the fragment placeholder in the layout page.
 * 
 * @author Emanuel Rabina
 */
public class FragmentProcessor extends AbstractProcessor {

	private static final String ATTRIBUTE_NAME_FRAGMENT = "fragment";

	/**
	 * Constructor, sets this processor to work on the 'fragment' attribute.
	 */
	public FragmentProcessor() {

		super(ATTRIBUTE_NAME_FRAGMENT);
	}

	/**
	 * Return the fragment with the matching name from the list of page
	 * fragments.
	 * 
	 * @param arguments
	 * @param attributename Name of the attribute on which the fragment name
	 * 						resides.
	 * @param fragments
	 * @param fragmentname	Name of the fragment to look for.
	 * @return Element with the given fragment, or <tt>null</tt> if no match
	 * 		   could be found.
	 */
	private Element findFragment(Arguments arguments, String attributename,
		List<Node> fragments, String fragmentname) {

		for (Node fragment: fragments) {
			if (fragment instanceof Element) {
				Element fragmentel = (Element)fragment;
				if (fragmentel.hasAttribute(attributename)) {
					FragmentSelection fragmentselection = StandardExpressionProcessor.parseFragmentSelection(
							arguments, fragmentel.getAttributeValue(attributename));
					if (fragmentselection.getTemplateName().toString().equals(fragmentname)) {
						return fragmentel;
					}
				}
			}
		}
		return null;
	}

	/**
	 * Includes or replaces the content of fragments into the corresponding
	 * fragment placeholder.
	 * 
	 * @param arguments
	 * @param element
	 * @param attributeName
	 * @return Processing result
	 */
	@Override
	@SuppressWarnings("unchecked")
	protected ProcessorResult processAttribute(Arguments arguments, Element element, String attributeName) {

		// Skip processing already-processed fragments
		Attribute fragmentattribute = element.getAttributeFromNormalizedName(attributeName);
		if (fragmentattribute == null) {
			return ProcessorResult.OK;
		}

		// Locate the page fragment that corresponds to this layout fragment
		FragmentSelection fragmentselection = StandardExpressionProcessor.parseFragmentSelection(
				arguments, fragmentattribute.getValue());
		Element pagefragment = findFragment(arguments, attributeName,
				(List<Node>)arguments.getContext().getVariables().get(CONTEXT_VAR_FRAGMENTS),
				fragmentselection.getTemplateName().toString());

		if (pagefragment != null) {

			// Replace the layout fragment with the page fragment
			Element pagefragmentclone = (Element)pagefragment.cloneNode(null, true);
			pagefragmentclone.removeAttribute(attributeName);

			element.clearChildren();
			element.addChild(pagefragmentclone);
			element.getParent().extractChild(element);
		}
		else {
			element.removeAttribute(attributeName);
		}

		return ProcessorResult.OK;
	}
}
