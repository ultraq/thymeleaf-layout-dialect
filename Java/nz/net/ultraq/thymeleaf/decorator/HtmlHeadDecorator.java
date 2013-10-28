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

import static nz.net.ultraq.thymeleaf.DecoratorUtilities.*;
import static nz.net.ultraq.thymeleaf.decorator.TitlePatternProcessor.CONTENT_TITLE;
import static nz.net.ultraq.thymeleaf.decorator.TitlePatternProcessor.DECORATOR_TITLE;

import org.thymeleaf.dom.Element;
import org.thymeleaf.dom.Node;
import org.thymeleaf.dom.Text;
import org.thymeleaf.standard.StandardDialect;
import org.thymeleaf.standard.processor.attr.StandardTextAttrProcessor;

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

		// If the content has no HEAD, then we don't need to do anything
		if (contenthead == null) {
			return;
		}

		// If the decorator has no HEAD, then we can just copy the content HEAD
		Element decoratorhead = findElement(decoratorhtml, HTML_ELEMENT_HEAD);
		if (decoratorhead == null) {
			decoratorhtml.insertChild(0, new Text(LINE_SEPARATOR));
			decoratorhtml.insertChild(1, contenthead);
			return;
		}

		// Merge the content and decorator titles into a single title element
		Element decoratortitle = findElement(decoratorhead, HTML_ELEMENT_TITLE);
		Element contenttitle   = findElement(contenthead, HTML_ELEMENT_TITLE);
		Element resultingtitle = null;
		if (decoratortitle != null || contenttitle != null) {
			resultingtitle = new Element(HTML_ELEMENT_TITLE);
			if (decoratortitle != null) {
				extractTitle(decoratorhead, decoratortitle, DECORATOR_TITLE, resultingtitle);
			}
			if (contenttitle != null) {
				extractTitle(contenthead, contenttitle, CONTENT_TITLE, resultingtitle);
			}
		}

		// Append the content's HEAD elements to the end of the decorator's HEAD
		// section, placing the resulting title at the beginning of it
		for (Node contentheadnode: contenthead.getChildren()) {
			decoratorhead.addChild(contentheadnode);
		}
		if (resultingtitle != null) {
			decoratorhead.insertChild(0, new Text(LINE_SEPARATOR));
			decoratorhead.insertChild(1, resultingtitle);
		}

		super.decorate(decoratorhead, contenthead);
	}

	/**
	 * Extract the title from the given TITLE element, whether it be the text
	 * in the tag body or a th:text in the attributes.
	 * 
	 * @param head         HEAD tag containing <tt>title</tt>.
	 * @param title        TITLE tag from which to extract the title.
	 * @param nodevariable Variable to store the title to as a node local
	 *                     variable in <tt>result</tt>.   
	 * @param result       The new TITLE element being constructed.
	 */
	private static void extractTitle(Element head, Element title, String nodevariable, Element result) {

		Text titletext = (Text)title.getFirstChild();
		result.clearChildren();
		result.addChild(titletext);
		if (hasAttribute(title, StandardDialect.PREFIX, StandardTextAttrProcessor.ATTR_NAME)) {
			result.setNodeLocalVariable(nodevariable, getAttributeValue(title,
					StandardDialect.PREFIX, StandardTextAttrProcessor.ATTR_NAME));
			removeAttribute(title, StandardDialect.PREFIX, StandardTextAttrProcessor.ATTR_NAME);
		}
		else {
			result.setNodeLocalVariable(nodevariable, titletext.getContent());
		}
		pullAttributes(result, title);
		head.removeChild(title);
	}
}
