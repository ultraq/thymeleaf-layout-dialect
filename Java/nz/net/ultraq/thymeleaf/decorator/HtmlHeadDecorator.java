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

import static nz.net.ultraq.thymeleaf.decorator.DecoratorUtilities.*;
import static nz.net.ultraq.thymeleaf.decorator.TitlePatternProcessor.DECORATOR_TITLE_NAME;
import static nz.net.ultraq.thymeleaf.decorator.TitlePatternProcessor.PROCESSOR_NAME_TITLEPATTERN_FULL;

import org.thymeleaf.dom.Attribute;
import org.thymeleaf.dom.Element;
import org.thymeleaf.dom.Node;
import org.thymeleaf.dom.Text;

import java.util.HashMap;

/**
 * A decorator specific to processing an HTML HEAD element.
 * 
 * @author Emanuel Rabina
 */
public class HtmlHeadDecorator extends ElementDecorator {

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

		// Append the content's HEAD elements to the end of the decorator's HEAD
		// section, replacing the decorator's TITLE element if necessary
		Element contenttitle = findElement(contenthead, HTML_ELEMENT_TITLE);
		if (contenttitle != null) {
			contenthead.removeChild(contenttitle);
			Element decoratortitle = findElement(decoratorhead, HTML_ELEMENT_TITLE);
			if (decoratortitle != null) {
				decoratorhead.insertBefore(decoratortitle, contenttitle);
				decoratorhead.removeChild(decoratortitle);

				// For title pattern processing, save the decorator's title so it can be retrieved later
				if (decoratortitle.hasChildren()) {
					HashMap<String,Object> decoratortitlemap = new HashMap<String,Object>();
					decoratortitlemap.put(DECORATOR_TITLE_NAME, ((Text)decoratortitle.getFirstChild()).getContent());
					contenttitle.setAllNodeLocalVariables(decoratortitlemap);

					// Let the content pattern override the decorator pattern
					Attribute contenttitlepattern = contenttitle.getAttributeMap().get(PROCESSOR_NAME_TITLEPATTERN_FULL);
					pullAttributes(contenttitle, decoratortitle);
					if (contenttitlepattern != null) {
						contenttitle.setAttribute(PROCESSOR_NAME_TITLEPATTERN_FULL, contenttitlepattern.getValue());
					}
				}
			}
			else {
				decoratorhead.insertChild(0, new Text(LINE_SEPARATOR));
				decoratorhead.insertChild(1, contenttitle);
			}
		}
		for (Node contentheadnode: contenthead.getChildren()) {
			decoratorhead.addChild(contentheadnode);
		}

		super.decorate(decoratorhead, contenthead);
	}
}
