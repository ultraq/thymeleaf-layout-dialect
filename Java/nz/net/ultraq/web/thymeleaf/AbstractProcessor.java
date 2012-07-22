
package nz.net.ultraq.web.thymeleaf;

import static nz.net.ultraq.web.thymeleaf.FragmentProcessor.ATTRIBUTE_NAME_FRAGMENT_FULL;
import static nz.net.ultraq.web.thymeleaf.FragmentProcessor.FRAGMENT_NAME_PREFIX;
import static nz.net.ultraq.web.thymeleaf.IncludeProcessor.ATTRIBUTE_NAME_INCLUDE_FULL;

import org.thymeleaf.dom.Element;
import org.thymeleaf.processor.attr.AbstractAttrProcessor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Common code for the layout processors.
 * 
 * @author Emanuel Rabina
 */
public abstract class AbstractProcessor extends AbstractAttrProcessor {

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
	 * Find and return all fragments within the given elements without delving
	 * into <tt>layout:include</tt> elements.
	 * 
	 * @param elements
	 * @return Map of prefixed fragment names and their fragment elements.
	 */
	protected Map<String,Object> findFragments(List<Element> elements) {

		HashMap<String,Object> fragments = new HashMap<String,Object>();
		findFragments(fragments, elements);
		return fragments;
	}

	/**
	 * Recursive search for all fragment elements without delving into
	 * <tt>layout:include</tt> elements.
	 * 
	 * @param fragments
	 * @param elements
	 */
	private void findFragments(HashMap<String,Object> fragments, List<Element> elements) {

		for (Element element: elements) {
			String fragmentname = element.getAttributeValue(ATTRIBUTE_NAME_FRAGMENT_FULL);
			if (fragmentname != null) {
				fragments.put(FRAGMENT_NAME_PREFIX + fragmentname, element);
			}
			if (!element.hasAttribute(ATTRIBUTE_NAME_INCLUDE_FULL)) {
				findFragments(fragments, element.getElementChildren());
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int getPrecedence() {

		return 0;
	}
}
