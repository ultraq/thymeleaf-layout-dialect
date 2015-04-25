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
import static nz.net.ultraq.thymeleaf.LayoutDialect.DIALECT_PREFIX_LAYOUT
import static nz.net.ultraq.thymeleaf.decorators.TitlePatternProcessor.TITLE_TYPE
import static nz.net.ultraq.thymeleaf.decorators.TitlePatternProcessor.TITLE_TYPE_CONTENT
import static nz.net.ultraq.thymeleaf.decorators.TitlePatternProcessor.TITLE_TYPE_DECORATOR
import static nz.net.ultraq.thymeleaf.decorators.TitlePatternProcessor.PROCESSOR_NAME_TITLEPATTERN

import org.thymeleaf.dom.Element
import org.thymeleaf.dom.Text

/**
 * A decorator specific to processing an HTML &lt;head&gt; element.
 * 
 * @author Emanuel Rabina
 */
class HtmlHeadDecorator extends XmlElementDecorator {

	/**
	 * Decorate the &lt;head&gt; part, appending all of the content
	 * &lt;head&gt; elements on to the decorator &lt;head&gt; elements.
	 * 
	 * @param decoratorHtml Decorator's &lt;html&gt; element.
	 * @param contentHead	Content's &lt;head&gt; element.
	 */
	@Override
	void decorate(Element decoratorHtml, Element contentHead) {

		// If the decorator has no <head>, then we can just use the content <head>
		def decoratorHead = decoratorHtml.findElement('head')
		if (!decoratorHead) {
			if (contentHead) {
				decoratorHtml.insertChildWithWhitespace(contentHead, 0)
				def contentTitle = contentHead.findElement('title')
				if (contentTitle) {
					contentTitle.removeAttribute(DIALECT_PREFIX_LAYOUT, PROCESSOR_NAME_TITLEPATTERN)
				}
			}
			return
		}

		// Copy the content and decorator <title>s
		def titleContainer = new Element('title-container')
		def titlePattern = null
		def titleExtraction = { headElement, titleType ->
			def titleElement = headElement?.findElement('title')
			if (titleElement) {
				headElement.removeChildWithWhitespace(titleElement)
				titlePattern = titleElement.getAttributeValue(DIALECT_PREFIX_LAYOUT, PROCESSOR_NAME_TITLEPATTERN) ?: titlePattern
				titleElement.removeAttribute(DIALECT_PREFIX_LAYOUT, PROCESSOR_NAME_TITLEPATTERN)
				titleElement.setNodeProperty(TITLE_TYPE, titleType)
				titleContainer.addChild(titleElement)
			}
			return titleElement
		}
		titleExtraction(decoratorHead, TITLE_TYPE_DECORATOR)
		titleExtraction(contentHead, TITLE_TYPE_CONTENT)

		def resultTitle = new Element('title')
		resultTitle.setAttribute("${DIALECT_PREFIX_LAYOUT}:${PROCESSOR_NAME_TITLEPATTERN}", titlePattern)

		// Merge the content's <head> elements with the decorator's <head>
		// section, placing the resulting title at the beginning of it
		if (contentHead) {
			contentHead.children.each { contentHeadNode ->
				if (contentHeadNode instanceof Element) {
					decoratorHead.insertChildWithWhitespace(contentHeadNode, findBestInsertionPoint(decoratorHead, contentHeadNode))
				}
				else {
					decoratorHead.addChild(contentHeadNode)
				}
			}
		}
		decoratorHead.insertChildWithWhitespace(resultTitle, 0)
		decoratorHead.insertChildWithWhitespace(titleContainer, 0)

		super.decorate(decoratorHead, contentHead)
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
