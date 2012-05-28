
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
 * Processor for the 'layout:fragment' attribute, replaces the content and tag
 * of the decorator fragment with those of the same name from the content page.
 * 
 * @author Emanuel Rabina
 */
public class FragmentProcessor extends AbstractProcessor {

	static final String ATTRIBUTE_NAME_FRAGMENT = "fragment";

	/**
	 * Constructor, sets this processor to work on the 'fragment' attribute.
	 */
	public FragmentProcessor() {

		super(ATTRIBUTE_NAME_FRAGMENT);
	}

	/**
	 * Return the fragment with the matching name from the list of gathered page
	 * fragments.
	 * 
	 * @param arguments
	 * @param attributename Name of the attribute on which the fragment name
	 * 						resides.
	 * @param fragmentname	Name of the fragment to look for.
	 * @return Element with the given fragment, or <tt>null</tt> if no match
	 * 		   could be found.
	 */
	@SuppressWarnings("unchecked")
	private Element findFragment(Arguments arguments, String attributename, String fragmentname) {

		List<Node> fragments = (List<Node>)arguments.getContext().getVariables().get(CONTEXT_VAR_FRAGMENTS);
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
	protected ProcessorResult processAttribute(Arguments arguments, Element element, String attributeName) {

		// Skip processing already-processed fragments
		Attribute fragmentattribute = element.getAttributeFromNormalizedName(attributeName);
		if (fragmentattribute == null) {
			return ProcessorResult.OK;
		}

		// Locate the page fragment that corresponds to this decorator fragment
		FragmentSelection fragmentselection = StandardExpressionProcessor.parseFragmentSelection(
				arguments, fragmentattribute.getValue());
		Element pagefragment = findFragment(arguments, attributeName,
				fragmentselection.getTemplateName().toString());

		// Replace the decorator fragment with the page fragment
		if (pagefragment != null) {
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
