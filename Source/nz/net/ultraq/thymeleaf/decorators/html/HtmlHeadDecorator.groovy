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

package nz.net.ultraq.thymeleaf.decorators.html

import nz.net.ultraq.thymeleaf.decorators.xml.XmlElementDecorator
import nz.net.ultraq.thymeleaf.fragments.FragmentMerger
import static nz.net.ultraq.thymeleaf.LayoutDialect.DIALECT_PREFIX_LAYOUT
import static nz.net.ultraq.thymeleaf.decorators.TitlePatternProcessor.CONTENT_TITLE
import static nz.net.ultraq.thymeleaf.decorators.TitlePatternProcessor.DECORATOR_TITLE
import static nz.net.ultraq.thymeleaf.decorators.TitlePatternProcessor.PROCESSOR_NAME_TITLEPATTERN

import org.thymeleaf.dom.Element
import org.thymeleaf.dom.Text
import org.thymeleaf.standard.StandardDialect
import org.thymeleaf.standard.processor.attr.StandardTextAttrProcessor
import org.thymeleaf.standard.processor.attr.StandardUtextAttrProcessor

/**
 * A decorator specific to processing an HTML HEAD element.
 * 
 * @author Emanuel Rabina
 */
class HtmlHeadDecorator extends XmlElementDecorator {

	/**
	 * Decorate the HEAD part.  This step replaces the decorator's TITLE element
	 * if the content has one, and appends all other content elements to the
	 * HEAD section, after all the decorator elements.
	 * 
	 * @param decoratorHtml Decorator's HTML element.
	 * @param contentHead	Content's HEAD element.
	 */
	@Override
	void decorate(Element decoratorHtml, Element contentHead) {

		// If the decorator has no HEAD, then we can just use the content HEAD
		def decoratorHead = decoratorHtml.findElement('head')
		if (!decoratorHead) {
			if (contentHead) {
				decoratorHtml.insertChild(0, new Text(System.properties.'line.separator'))
				decoratorHtml.insertChild(1, contentHead)

				def contentTitle = contentHead.findElement('title')
				if (contentTitle) {
					def resultingTitle = new Element('title')
					extractTitle(contentHead, contentTitle, CONTENT_TITLE, resultingTitle)
					contentHead.insertChild(0, new Text(System.properties.'line.separator'))
					contentHead.insertChild(1, resultingTitle)
				}
			}
			return
		}

		// Merge the content and decorator titles into a single title element
		def decoratorTitle = decoratorHead.findElement('title')
		def contentTitle   = null
		if (contentHead) {
			contentTitle = contentHead.findElement('title')
		}
		def resultingTitle = null
		if (decoratorTitle || contentTitle) {
			resultingTitle = new Element('title')
			if (decoratorTitle) {
				extractTitle(decoratorHead, decoratorTitle, DECORATOR_TITLE, resultingTitle)
			}
			if (contentTitle != null) {
				extractTitle(contentHead, contentTitle, CONTENT_TITLE, resultingTitle)
			}

			// If there's a title pattern, get rid of all other text setters so
			// they don't interfere with it
			if (resultingTitle.hasAttribute(DIALECT_PREFIX_LAYOUT, PROCESSOR_NAME_TITLEPATTERN)) {
				resultingTitle.removeAttribute(StandardDialect.PREFIX, StandardTextAttrProcessor.ATTR_NAME)
				resultingTitle.removeAttribute(StandardDialect.PREFIX, StandardUtextAttrProcessor.ATTR_NAME)
			}
		}

		// Merge the content's HEAD elements with the decorator's HEAD section,
		// placing the resulting title at the beginning of it
		if (contentHead) {
			contentHead.children.each { contentHeadNode ->
				if (contentHeadNode instanceof Element) {
					int insertionpoint = findBestInsertionPoint(decoratorHead, contentHeadNode)
					insertElementWithWhitespace(decoratorHead, contentHeadNode, insertionpoint)
				}
				else {
					decoratorHead.addChild(contentHeadNode)
				}				
			}
		}
		if (resultingTitle) {
			insertElementWithWhitespace(decoratorHead, resultingTitle, 0)
		}

		super.decorate(decoratorHead, contentHead)
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
		def titleText = title.firstChild
		result.clearChildren()
		result.addChild(titleText)

		// Extract any text or processors from the title element's attributes
		if (title.hasAttribute(StandardDialect.PREFIX, StandardUtextAttrProcessor.ATTR_NAME)) {
			result.setNodeProperty(titlekey, title.getAttributeValue(
				StandardDialect.PREFIX, StandardUtextAttrProcessor.ATTR_NAME))
		}
		else if (title.hasAttribute(StandardDialect.PREFIX, StandardTextAttrProcessor.ATTR_NAME)) {
			result.setNodeProperty(titlekey, title.getAttributeValue(
					StandardDialect.PREFIX, StandardTextAttrProcessor.ATTR_NAME))
		}

		// Extract text from a previously set value (deep hierarchies)
		else if (title.hasNodeProperty(titlekey)) {
			result.setNodeProperty(titlekey, title.getNodeProperty(titlekey))
		}

		// Extract text from the title element's content
		else if (titleText) {
			result.setNodeProperty(titlekey, titleText.content)
		}

		new FragmentMerger().mergeAttributes(result, title)
		head.removeChild(title)
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
	private static int findBestInsertionPoint(Element head, Element element) {

		// TODO: Expand this to include all other element types.  Will likely
		//       leave this until the Groovy rewrite as I know how to solve this
		//       problem in a functional programming style - it'll be too much
		//       code in plain Java.

		def type = HeadElement.findMatchingType(element)

		def indexOfLastOfType  = -1
		def indexOfLastElement = -1
		def indexOfLastGap     = -1
		def headNodes = head.children
		headNodes.eachWithIndex { headnode, i ->
			if (headnode instanceof Element) {
				indexOfLastElement = i
				if (HeadElement.findMatchingType(headnode) == type) {
					indexOfLastOfType = i
				}
			}
			if (i == headNodes.size() - 1 && headnode instanceof Text) {
				indexOfLastGap = i
			}
		}

		return indexOfLastOfType  != -1 ? indexOfLastOfType  + 1 :	// After last matching type
		       indexOfLastElement != -1 ? indexOfLastElement + 1 :	// After last element
		       indexOfLastGap     != -1 ? indexOfLastGap :			// At the last gap
		       headNodes.size()										// At the end
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
	private static void insertElementWithWhitespace(Element parent, Element child, int insertionpoint) {

		def children = parent.children

		// Insert a whitespace gap between elements if available
		if (children) {
			def whitespace = children.get(Math.min(insertionpoint, children.size() - 1))
			if (whitespace instanceof Text) {
				parent.insertChild(insertionpoint, whitespace.cloneNode(null, false))
				parent.insertChild(insertionpoint + 1, child)
				return
			}
		}
		parent.insertChild(insertionpoint, child)
	}


	/**
	 * Enum for the types of elements in the HEAD section that we might need to
	 * sort.
	 */
	private static enum HeadElement {

		STYLESHEET,
		SCRIPT,
		OTHER

		/**
		 * Figure out the enum for the given element type.
		 *
		 * @param element The element to match.
		 * @return Matching <tt>HeadElement</tt> enum to descript the element.
		 */
		private static HeadElement findMatchingType(Element element) {

			def elementName = element.normalizedName
			if (elementName == 'script') {
				return SCRIPT
			}
			else if (elementName == 'stylesheet' ||
					(elementName == 'link' &&
							element.hasAttribute('rel') &&
							element.getAttributeValue('rel') == 'stylesheet')) {
				return STYLESHEET
			}
			return OTHER
		}
	}
}
