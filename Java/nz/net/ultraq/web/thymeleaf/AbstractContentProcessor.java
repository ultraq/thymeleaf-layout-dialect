
package nz.net.ultraq.web.thymeleaf;

import static nz.net.ultraq.web.thymeleaf.FragmentProcessor.ATTRIBUTE_NAME_FRAGMENT_FULL;
import static nz.net.ultraq.web.thymeleaf.FragmentProcessor.FRAGMENT_NAME_PREFIX;
import static nz.net.ultraq.web.thymeleaf.IncludeProcessor.ATTRIBUTE_NAME_INCLUDE_FULL;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.thymeleaf.dom.Attribute;
import org.thymeleaf.dom.Element;
import org.thymeleaf.standard.StandardDialect;
import org.thymeleaf.standard.processor.attr.StandardWithAttrProcessor;

/**
 * Common code for the processors that fetch the content of other pages to
 * include into the current template.
 * 
 * @author Emanuel Rabina
 */
public abstract class AbstractContentProcessor extends AbstractProcessor {

	private static final String TH_WITH = StandardDialect.PREFIX + ":" + StandardWithAttrProcessor.ATTR_NAME;

	/**
	 * Subclass constructor, set the attribute name that this processor will
	 * respond to.
	 * 
	 * @param attribute
	 */
	protected AbstractContentProcessor(String attribute) {

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

	/**
	 * Merge the content element attributes with those of the target element
	 * 
	 * @param contentelement
	 * @param targetelement
	 */
	protected static void mergeAttributes(Element contentelement, Element targetelement) {

		if (contentelement == null || targetelement == null) {
			return;
		}

		for (Attribute contentattribute: contentelement.getAttributeMap().values()) {
			String attributename = contentattribute.getOriginalName();

			// Merge th:with attributes to retain local variable declarations
			if (attributename.equals(TH_WITH) && targetelement.hasAttribute(TH_WITH)) {
				targetelement.setAttribute(attributename, contentattribute.getValue() + "," +
						targetelement.getAttributeValue(TH_WITH));
			}
			else {
				targetelement.setAttribute(contentattribute.getOriginalName(), contentattribute.getValue());
			}
		}
	}
}
