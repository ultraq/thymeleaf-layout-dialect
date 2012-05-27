
package nz.net.ultraq.web.thymeleaf;

import org.thymeleaf.Arguments;
import org.thymeleaf.Template;
import org.thymeleaf.TemplateProcessingParameters;
import org.thymeleaf.dom.Element;
import org.thymeleaf.dom.Node;
import org.thymeleaf.processor.ProcessorResult;

import java.util.List;

/**
 * Processor for the 'layout:decorator' attribute.  Locates the page identified
 * by the attribute and decorates the current page with it.
 * <p>
 * The page identified by the <tt>layout:decorator</tt> attribute is resolved
 * using the same template resolver as Thymeleaf, so be sure to locate your
 * decorator pages the same way you would your Thymeleaf page fragments.
 * 
 * @author Emanuel Rabina
 */
public class LayoutProcessor extends AbstractProcessor {

	private static final String ATTRIBUTE_NAME_DECORATOR = "decorator";

	private static final String HTML_ELEMENT_HEAD  = "head";
	private static final String HTML_ELEMENT_TITLE = "title";
	private static final String HTML_ELEMENT_BODY  = "body";

	/**
	 * Constructor, sets this processor to work on the 'decorator' attribute.
	 */
	public LayoutProcessor() {

		super(ATTRIBUTE_NAME_DECORATOR);
	}

	/**
	 * Decorate the BODY part of the page.  This is effectively a replacement of
	 * the page's BODY elements with those of the decorator's, putting back only
	 * the page elements specified by <tt>layout:fragment</tt> into the places
	 * specified by the decorator.
	 * 
	 * @param arguments
	 * @param decoratorbody Decorator's BODY element.
	 * @param pagebody		Page's BODY element.
	 */
	private void decorateBody(Arguments arguments, Element decoratorbody, Element pagebody) {

		List<Node> pagefragments = pagebody.getChildren();

		// Copy the layout's BODY into the page's BODY
		pagebody.clearChildren();
		for (Node node: decoratorbody.getChildren()) {
			pagebody.addChild(node.cloneNode(null, true));
		}

		// Store the page fragments for later use
		arguments.getContext().getVariables().put(CONTEXT_VAR_FRAGMENTS, pagefragments);
	}

	/**
	 * Decorate the HEAD part of the page with the HEAD part of the decorator.
	 * This step uses the decorator's TITLE element if the page is lacking one,
	 * then inserts the decorator's HEAD elements after the title and before the
	 * page ones.
	 * 
	 * @param decoratorhead Decorator's HTML HEAD element.
	 * @param pagehead		Page's HTML HEAD element.
	 */
	private void decorateHead(Element decoratorhead, Element pagehead) {

		Element pagetitle = findElement(pagehead, HTML_ELEMENT_TITLE);
		pagehead.removeChild(pagetitle);

		// Insert the decorator's HEAD elements into the page's HEAD section
		for (Node decoratornode: decoratorhead.getChildren()) {

			// Skip copying the decorator's TITLE element if the page already has one
			if (pagetitle != null && decoratornode instanceof Element &&
				((Element)decoratornode).getOriginalName().equals(HTML_ELEMENT_TITLE)) {
				continue;
			}
			pagehead.insertChild(0, decoratornode);
		}

		pagehead.insertChild(0, pagetitle);
	}

	/**
	 * Recursive search for an element within the given node in the DOM tree.
	 * 
	 * @param element Node to initiate the search from.
	 * @param name	  Name of the element to look for.
	 * @return Element with the given name, or <tt>null</tt> if the element
	 * 		   could not be found.
	 */
	private Element findElement(Element element, String name) {

		if (element.getOriginalName().equals(name)) {
			return element;
		}
		for (Element child: element.getElementChildren()) {
			Element result = findElement(child, name);
			if (result != null) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Locates the decorator page specified by the layout attribute and applies
	 * it to the current page being processed.
	 * 
	 * @param arguments
	 * @param element
	 * @param attributeName
	 * @return Result of the processing.
	 */
	@Override
	protected ProcessorResult processAttribute(Arguments arguments, Element element, String attributeName) {

		// Locate the decorator page
		String decoratorname = element.getAttributeValue(attributeName);
		Template decorator = arguments.getTemplateRepository().getTemplate(new TemplateProcessingParameters(
				arguments.getConfiguration(), decoratorname, arguments.getContext()));
		Element decoratorhtmlelement = decorator.getDocument().getFirstElementChild();

		// Decorate the HEAD element of the page with the HEAD of the decorator
		decorateHead(findElement(decoratorhtmlelement, HTML_ELEMENT_HEAD),
				findElement(element, HTML_ELEMENT_HEAD));

		// Decorate the BODY of the page, putting fragments in the correct place
		decorateBody(arguments, findElement(decoratorhtmlelement, HTML_ELEMENT_BODY),
				findElement(element, HTML_ELEMENT_BODY));

		// Remove the decorator attribute
		element.removeAttribute(attributeName);

		return ProcessorResult.OK;
	}
}
