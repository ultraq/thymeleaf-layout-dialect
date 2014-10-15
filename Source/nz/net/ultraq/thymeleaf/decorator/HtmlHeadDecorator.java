/*
 * Copyright 2013, Emanuel Rabina (http://www.ultraq.net.nz/)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package nz.net.ultraq.thymeleaf.decorator;

import static nz.net.ultraq.thymeleaf.LayoutDialect.DIALECT_PREFIX_LAYOUT;
import static nz.net.ultraq.thymeleaf.LayoutUtilities.*;
import static nz.net.ultraq.thymeleaf.decorator.TitlePatternProcessor.CONTENT_TITLE;
import static nz.net.ultraq.thymeleaf.decorator.TitlePatternProcessor.DECORATOR_TITLE;
import static nz.net.ultraq.thymeleaf.decorator.TitlePatternProcessor.PROCESSOR_NAME_TITLEPATTERN;

import org.thymeleaf.dom.Element;
import org.thymeleaf.dom.Node;
import org.thymeleaf.dom.Text;
import org.thymeleaf.standard.StandardDialect;
import org.thymeleaf.standard.processor.attr.StandardTextAttrProcessor;
import org.thymeleaf.standard.processor.attr.StandardUtextAttrProcessor;

import java.util.List;

/**
 * A decorator specific to processing an HTML HEAD element.
 * 
 * @author Emanuel Rabina
 */
public class HtmlHeadDecorator extends XmlElementDecorator {

	/**
	 * Decorate the HEAD part.  This step replaces the decorator's TITLE element
	 * if the content has one, and appends all other content elements to the
	 * HEAD section, after all the decorator elements.
	 * 
	 * @param decoratorhtml Decorator's HTML element.
	 * @param contenthead	Content's HEAD element.
	 */
	@Override
	public void decorate(Element decoratorhtml, Element contenthead) {

		// If the decorator has no HEAD, then we can just use the content HEAD
		Element decoratorhead = findElement(decoratorhtml, HTML_ELEMENT_HEAD);
		if (decoratorhead == null) {
			if (contenthead != null) {
				decoratorhtml.insertChild(0, new Text(LINE_SEPARATOR));
				decoratorhtml.insertChild(1, contenthead);

				Element contenttitle = findElement(contenthead, HTML_ELEMENT_TITLE);
				if (contenttitle != null) {
					Element resultingtitle = new Element(HTML_ELEMENT_TITLE);
					extractTitle(contenthead, contenttitle, CONTENT_TITLE, resultingtitle);
					contenthead.insertChild(0, new Text(LINE_SEPARATOR));
					contenthead.insertChild(1, resultingtitle);
				}
			}
			return;
		}

		// Merge the content and decorator titles into a single title element
		Element decoratortitle = findElement(decoratorhead, HTML_ELEMENT_TITLE);
		Element contenttitle   = null;
		if (contenthead != null) {
			contenttitle = findElement(contenthead, HTML_ELEMENT_TITLE);
		}
		Element resultingtitle = null;
		if (decoratortitle != null || contenttitle != null) {
			resultingtitle = new Element(HTML_ELEMENT_TITLE);
			if (decoratortitle != null) {
				extractTitle(decoratorhead, decoratortitle, DECORATOR_TITLE, resultingtitle);
			}
			if (contenttitle != null) {
				extractTitle(contenthead, contenttitle, CONTENT_TITLE, resultingtitle);
			}

			// If there's a title pattern, get rid of all other text setters so
			// they don't interfere with it
			if (hasAttribute(resultingtitle, DIALECT_PREFIX_LAYOUT, PROCESSOR_NAME_TITLEPATTERN)) {
				removeAttribute(resultingtitle, StandardDialect.PREFIX, StandardTextAttrProcessor.ATTR_NAME);
				removeAttribute(resultingtitle, StandardDialect.PREFIX, StandardUtextAttrProcessor.ATTR_NAME);
			}
		}

		// Merge the content's HEAD elements with the decorator's HEAD section,
		// placing the resulting title at the beginning of it
		if (contenthead != null) {
			for (Element contentheadelement: contenthead.getElementChildren()) {
				int insertionpoint = findBestInsertionPoint(decoratorhead, contentheadelement);
				insertElementWithWhitespace(decoratorhead, contentheadelement, insertionpoint);
			}
		}
		if (resultingtitle != null) {
			insertElementWithWhitespace(decoratorhead, resultingtitle, 0);
		}

		super.decorate(decoratorhead, contenthead);
	}

	/**
	 * Extract the title from the given TITLE element, whether it be the text
	 * in the tag body or a th:text in the attributes.
	 * 
	 * @param head     HEAD tag containing <tt>title</tt>.
	 * @param title    TITLE tag from which to extract the title.
	 * @param titlekey Key to store the title to as a node property in
	 *                 <tt>result</tt>.   
	 * @param result   The new TITLE element being constructed.
	 */
	private static void extractTitle(Element head, Element title, String titlekey, Element result) {

		// Make the result look like the title
		Text titletext = (Text)title.getFirstChild();
		result.clearChildren();
		result.addChild(titletext);

		// Extract any text or processors from the title element's attributes
		if (hasAttribute(title, StandardDialect.PREFIX, StandardUtextAttrProcessor.ATTR_NAME)) {
			result.setNodeProperty(titlekey, getAttributeValue(title,
					StandardDialect.PREFIX, StandardUtextAttrProcessor.ATTR_NAME));
		}
		else if (hasAttribute(title, StandardDialect.PREFIX, StandardTextAttrProcessor.ATTR_NAME)) {
			result.setNodeProperty(titlekey, getAttributeValue(title,
					StandardDialect.PREFIX, StandardTextAttrProcessor.ATTR_NAME));
		}

		// Extract text from a previously set value (deep hierarchies)
		else if (title.hasNodeProperty(titlekey)) {
			result.setNodeProperty(titlekey, title.getNodeProperty(titlekey));
		}

		// Extract text from the title element's content
		else if (titletext != null) {
			result.setNodeProperty(titlekey, titletext.getContent());
		}

		pullAttributes(result, title);
		head.removeChild(title);
	}

	/**
	 * Attempt to find the best place to merge a content element into the
	 * decorator HEAD section, ensuring like elements are grouped together.
	 * Currently, only stylesheets and scripts are subject to this logic.
	 * Everything else just results in a value that would put it at the end of
	 * the HEAD section.
	 * 
	 * @param head    HEAD element.
	 * @param element The element to insert.
	 * @return Best guess at where the node should be inserted in the HEAD.
	 */
	private int findBestInsertionPoint(Element head, Element element) {

		// TODO: Expand this to include all other element types.  Will likely
		//       leave this until the Groovy rewrite as I know how to solve this
		//       problem in a functional programming style - it'll be too much
		//       code in plain Java.

		HeadElement type = HeadElement.findMatchingType(element);

		int indexoflastoftype  = -1;
		int indexoflastelement = -1;
		int indexoflastgap     = -1;
		List<Node> headnodes = head.getChildren();
		for (int i = 0; i < headnodes.size(); i++) {
			Node headnode = headnodes.get(i);
			if (headnode instanceof Element) {
				indexoflastelement = i;
				if (HeadElement.findMatchingType((Element)headnode) == type) {
					indexoflastoftype = i;
				}
			}
			if (i == headnodes.size() - 1 && headnode instanceof Text) {
				indexoflastgap = i;
			}
		}

		return indexoflastoftype  != -1 ? indexoflastoftype  + 1 :	// After last matching type
		       indexoflastelement != -1 ? indexoflastelement + 1 :	// After last element
		       indexoflastgap     != -1 ? indexoflastgap :			// At the last gap
		       headnodes.size();									// At the end
	}

	/**
	 * Inserts an element as a child of another element, creating a matching
	 * whitespace node before it so that it appears in line with all the
	 * existing children.
	 * 
	 * @param parent         Element group to add the child to.
	 * @param child          Element to add.
	 * @param insertionpoint Point at which to insert the child element.
	 */
	private void insertElementWithWhitespace(Element parent, Element child, int insertionpoint) {

		List<Node> children = parent.getChildren();

		// Insert a whitespace gap between elements if available
		if (!children.isEmpty()) {
			Node whitespace = children.get(Math.min(insertionpoint, children.size() - 1));
			if (whitespace instanceof Text) {
				parent.insertChild(insertionpoint, whitespace.cloneNode(null, false));
				parent.insertChild(insertionpoint + 1, child);
				return;
			}
		}
		parent.insertChild(insertionpoint, child);
	}


	/**
	 * Enum for the types of elements in the HEAD section that we might need to
	 * sort.
	 */
	private static enum HeadElement {

		STYLESHEET,
		SCRIPT,
		OTHER;

		private static final String HEAD_ELEMENT_SCRIPT      = "script";
		private static final String HEAD_ELEMENT_LINK        = "link";
		private static final String HEAD_ELEMENT_STYLESHEET  = "style";
		private static final String REL_ATTRIBUTE            = "rel";
		private static final String REL_ATTRIBUTE_STYLESHEET = "stylesheet";

		/**
		 * Figure out the enum for the given element type.
		 *
		 * @param element The element to match.
		 * @return Matching <tt>HeadElement</tt> enum to descript the element.
		 */
		private static HeadElement findMatchingType(Element element) {

			String elementname = element.getNormalizedName();
			if (elementname.equals(HEAD_ELEMENT_SCRIPT)) {
				return SCRIPT;
			}
			else if (elementname.equals(HEAD_ELEMENT_STYLESHEET) ||
					(elementname.equals(HEAD_ELEMENT_LINK) &&
							element.hasAttribute(REL_ATTRIBUTE) &&
							element.getAttributeValue(REL_ATTRIBUTE).equals(REL_ATTRIBUTE_STYLESHEET))) {
				return STYLESHEET;
			}
			return OTHER;
		}
	}
}
