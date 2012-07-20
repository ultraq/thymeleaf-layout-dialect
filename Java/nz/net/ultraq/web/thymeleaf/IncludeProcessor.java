
package nz.net.ultraq.web.thymeleaf;

import org.thymeleaf.Arguments;
import org.thymeleaf.Template;
import org.thymeleaf.TemplateProcessingParameters;
import org.thymeleaf.dom.Attribute;
import org.thymeleaf.dom.Element;
import org.thymeleaf.dom.Node;
import org.thymeleaf.processor.ProcessorResult;

import java.util.Map;

/**
 * Processor for the 'layout:include' attribute, does the same as Thymeleaf's
 * include tag but allows for the passing of element fragments to the included
 * page.
 * 
 * @author Emanuel Rabina
 */
public class IncludeProcessor extends AbstractProcessor {

	static final String ATTRIBUTE_NAME_INCLUDE = "include";

	/**
	 * Constructor, sets this processor to work on the 'include' attribute.
	 */
	public IncludeProcessor() {

		super(ATTRIBUTE_NAME_INCLUDE);
	}

	/**
	 * Recursive search for an element with the given attribute name and value.
	 * 
	 * @param element Node to initiate the search from.
	 * @param name	  Name of the attribute to look for.
	 * @param value	  Value the attribute must have.
	 * @return Element with the given attribute, or <tt>null</tt> if no element
	 * 		   contains the attribute name.
	 */
	private Element findElementWithAttribute(Element element, String name, String value) {

		Map<String,Attribute> attributemap = element.getAttributeMap();
		if (attributemap.containsKey(name) && attributemap.get(name).getValue().equals(value)) {
			return element;
		}
		for (Element child: element.getElementChildren()) {
			Element result = findElementWithAttribute(child, name, value);
			if (result != null) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Locates the specified page and includes it into the current template.
	 * 
	 * @param arguments
	 * @param element
	 * @param attributeName
	 */
	@Override
	protected ProcessorResult processAttribute(Arguments arguments, Element element, String attributeName) {

		// Locate the page and fragment to include
		String fragmentlocation = element.getAttributeValue(attributeName);
		String templatename = fragmentlocation.substring(0, fragmentlocation.indexOf(':')).trim();
		String fragmentname = fragmentlocation.substring(fragmentlocation.lastIndexOf(':')).trim();

		Template template = arguments.getTemplateRepository().getTemplate(new TemplateProcessingParameters(
				arguments.getConfiguration(), templatename, arguments.getContext()));
		Element fragment = findElementWithAttribute(template.getDocument().getFirstElementChild(),
				LayoutDialect.LAYOUT_PREFIX + ":" + FragmentProcessor.ATTRIBUTE_NAME_FRAGMENT,
				fragmentname);

		// Gather all fragment parts from the scope of the include attribute and store for later use
		findFragments(getFragmentList(arguments), element);

		// Place the included page fragment into this element and replace it
		element.clearChildren();
		for (Node node: fragment.getChildren()) {
			element.addChild(node.cloneNode(null, true));
		}

		// Remove the include attribute
		element.removeAttribute(attributeName);

		return ProcessorResult.OK;
	}
}
