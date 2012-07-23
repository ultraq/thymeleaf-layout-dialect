
package nz.net.ultraq.web.thymeleaf;

import static nz.net.ultraq.web.thymeleaf.LayoutDialect.LAYOUT_PREFIX;

import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.processor.ProcessorResult;

/**
 * Processor for the 'layout:fragment' attribute, replaces the content and tag
 * of the decorator fragment with those of the same name from the content page.
 * 
 * @author Emanuel Rabina
 */
public class FragmentProcessor extends AbstractProcessor {

	static final String ATTRIBUTE_NAME_FRAGMENT = "fragment";
	static final String ATTRIBUTE_NAME_FRAGMENT_FULL = LAYOUT_PREFIX + ":" + ATTRIBUTE_NAME_FRAGMENT;

	static final String FRAGMENT_NAME_PREFIX = "fragment-name::";

	/**
	 * Constructor, sets this processor to work on the 'fragment' attribute.
	 */
	public FragmentProcessor() {

		super(ATTRIBUTE_NAME_FRAGMENT);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int getPrecedence() {

		return 1;
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

		// Locate the page fragment that corresponds to this decorator/include fragment
		String fragmentname = element.getAttributeValue(attributeName);
		Element pagefragment = (Element)arguments.getLocalVariable(FRAGMENT_NAME_PREFIX + fragmentname);
		element.removeAttribute(attributeName);

		// Replace the decorator/include fragment with the page fragment
		if (pagefragment != null) {
			pagefragment.removeAttribute(attributeName);
			pullTargetContent(element, pagefragment);
		}

		return ProcessorResult.OK;
	}
}
