
package nz.net.ultraq.web.thymeleaf;

import org.thymeleaf.Arguments;
import org.thymeleaf.Template;
import org.thymeleaf.TemplateProcessingParameters;
import org.thymeleaf.dom.Element;
import org.thymeleaf.dom.NestableNode;
import org.thymeleaf.dom.Node;
import org.thymeleaf.dom.Text;
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
public class DecoratorProcessor extends AbstractProcessor {

	private static final String LINE_SEPARATOR = System.getProperty("line.separator");

	private static final String HTML_ELEMENT_HTML  = "html";
	private static final String HTML_ELEMENT_HEAD  = "head";
	private static final String HTML_ELEMENT_TITLE = "title";
	private static final String HTML_ELEMENT_BODY  = "body";

	static final String ATTRIBUTE_NAME_DECORATOR = "decorator";

	/**
	 * Constructor, sets this processor to work on the 'decorator' attribute.
	 */
	public DecoratorProcessor() {

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
	 * @param pagehtml		Page's HTML element.
	 */
	private void decorateBody(Arguments arguments, Element decoratorbody, Element pagehtml) {

		// If the decorator has no BODY, then we don't need to do anything
		if (decoratorbody == null) {
			return;
		}

		Element pagebody = findElement(pagehtml, HTML_ELEMENT_BODY);

		// If the page has no BODY, we can just copy over the decorator BODY
		if (pagebody == null) {
			insertAsLastItem(pagehtml, new Text(LINE_SEPARATOR));
			insertAsLastItem(pagehtml, decoratorbody.cloneNode(null, true));
			return;
		}

		// Gather all fragment parts from the page and store for later use
		findFragments(getFragmentList(arguments), pagebody);

		// Copy the decorator BODY into the page BODY
		pagebody.clearChildren();
		for (Node node: decoratorbody.getChildren()) {
			pagebody.addChild(node.cloneNode(null, true));
		}
	}

	/**
	 * Decorate the HEAD part of the page with the HEAD part of the decorator.
	 * This step uses the decorator's TITLE element if the page is lacking one,
	 * then inserts the decorator's HEAD elements after the title and before the
	 * page ones.
	 * 
	 * @param decoratorhead Decorator's HTML HEAD element.
	 * @param pagehtml		Page's HTML element.
	 */
	private void decorateHead(Element decoratorhead, Element pagehtml) {

		// If the decorator has no HEAD, then we don't need to do anything
		if (decoratorhead == null) {
			return;
		}

		Element pagehead = findElement(pagehtml, HTML_ELEMENT_HEAD);

		// If the page has no HEAD, we can just copy over the decorator HEAD
		if (pagehead == null) {
			insertAsFirstItem(pagehtml, new Text(LINE_SEPARATOR));
			insertAsFirstItem(pagehtml, decoratorhead.cloneNode(null, true));
			return;
		}

		Element pagetitle = findElement(pagehead, HTML_ELEMENT_TITLE);
		if (pagetitle != null) {
			pagehead.removeChild(pagetitle);
		}

		// Insert the decorator's HEAD elements into the page's HEAD section
		List<Node> decoratorheadelements = decoratorhead.getChildren();
		for (int i = decoratorheadelements.size() - 1; i >= 0; i--) {
			Node decoratorheadelement = decoratorheadelements.get(i);

			// Skip copying the decorator's TITLE element if the page already has one
			if (pagetitle != null && decoratorheadelement instanceof Element &&
				((Element)decoratorheadelement).getOriginalName().equals(HTML_ELEMENT_TITLE)) {
				continue;
			}
			insertAsFirstItem(pagehead, decoratorheadelement.cloneNode(null, true));
		}

		// Put the TITLE back at the top of HEAD
		if (pagetitle != null) {
			insertAsFirstItem(pagehead, pagetitle);
		}
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
	 * Insert a node before other nodes in a node group, not including any
	 * initial whitespace so that things end-up nicely indented.
	 * 
	 * @param nodegroup Node group to insert into.
	 * @param node		Node to insert.
	 */
	private void insertAsFirstItem(NestableNode nodegroup, Node node) {

		if (nodegroup.hasChildren()) {
			Node firstnode = nodegroup.getFirstChild();
			if (firstnode instanceof Text) {
				nodegroup.insertAfter(firstnode, node);
			}
			else {
				nodegroup.insertBefore(firstnode, node);
			}
		}
		else {
			nodegroup.addChild(node);
		}
	}

	/**
	 * Insert a node after other nodes in a node group, not including any final
	 * whitespace so that things end-up nicely indented.
	 * 
	 * @param nodegroup Node group to insert into.
	 * @param node		Node to insert.
	 */
	private void insertAsLastItem(NestableNode nodegroup, Node node) {

		if (nodegroup.hasChildren()) {
			Node lastnode = nodegroup.getChildren().get(nodegroup.numChildren() - 1);
			if (lastnode instanceof Text) {
				nodegroup.insertBefore(lastnode, node);
			}
			else {
				nodegroup.insertAfter(lastnode, node);
			}
		}
		else {
			nodegroup.addChild(node);
		}
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

		if (!element.getOriginalName().equals(HTML_ELEMENT_HTML)) {
			throw new IllegalArgumentException("layout:decorator attribute must appear in an <html> root element of your content page");
		}

		// Locate the decorator page
		String decoratorname = element.getAttributeValue(attributeName);
		Template decorator = arguments.getTemplateRepository().getTemplate(new TemplateProcessingParameters(
				arguments.getConfiguration(), decoratorname, arguments.getContext()));
		Element decoratorhtmlelement = decorator.getDocument().getFirstElementChild();

		if (decoratorhtmlelement == null || !decoratorhtmlelement.getOriginalName().equals(HTML_ELEMENT_HTML)) {
			throw new IllegalArgumentException("Decorator page " + decoratorname + " must have an <html> root element");
		}

		// Decorate the HEAD element of the page with the HEAD of the decorator
		decorateHead(findElement(decoratorhtmlelement, HTML_ELEMENT_HEAD), element);

		// Decorate the BODY of the page, putting fragments in the correct place
		decorateBody(arguments, findElement(decoratorhtmlelement, HTML_ELEMENT_BODY), element);

		// Remove the decorator attribute
		element.removeAttribute(attributeName);

		return ProcessorResult.OK;
	}
}
