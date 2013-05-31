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

import static nz.net.ultraq.thymeleaf.TitlePatternProcessor.DECORATOR_TITLE_NAME;
import static nz.net.ultraq.thymeleaf.TitlePatternProcessor.PROCESSOR_NAME_TITLEPATTERN_FULL;
import static nz.net.ultraq.thymeleaf.decorator.DecoratorUtilities.*;

import org.thymeleaf.dom.Attribute;
import org.thymeleaf.dom.Element;
import org.thymeleaf.dom.Node;
import org.thymeleaf.dom.Text;

import java.util.HashMap;

/**
 * A decorator specific to processing an HTML &lt;head&gt; element.
 * 
 * @author Emanuel Rabina
 */
public class HtmlHeadDecorator extends Decorator {

	/**
	 * Decorate the HEAD part.  This step replaces the decorator's TITLE element
	 * if the page has one, and appends all other page elements to the HEAD
	 * section, after all the decorator elements.
	 * 
	 * @param decoratorhtml Decorator's HTML element.
	 * @param pagehead		Page's HEAD element.
	 */
	@Override
	public void decorate(Element decoratorhtml, Element pagehead) {

		// If the page has no HEAD, then we don't need to do anything
		if (pagehead == null) {
			return;
		}

		// If the decorator has no HEAD, then we can just copy the page HEAD
		Element decoratorhead = findElement(decoratorhtml, HTML_ELEMENT_HEAD);
		if (decoratorhead == null) {
			decoratorhtml.insertChild(0, new Text(LINE_SEPARATOR));
			decoratorhtml.insertChild(1, pagehead);
			return;
		}

		// Append the page's HEAD elements to the end of the decorator's HEAD section,
		// replacing the decorator's TITLE element if necessary
		Element pagetitle = findElement(pagehead, HTML_ELEMENT_TITLE);
		if (pagetitle != null) {
			pagehead.removeChild(pagetitle);
			Element decoratortitle = findElement(decoratorhead, HTML_ELEMENT_TITLE);
			if (decoratortitle != null) {
				decoratorhead.insertBefore(decoratortitle, pagetitle);
				decoratorhead.removeChild(decoratortitle);

				// For title pattern processing, save the decorator's title so it can be retrieved later
				if (decoratortitle.hasChildren()) {
					HashMap<String,Object> decoratortitlemap = new HashMap<String,Object>();
					decoratortitlemap.put(DECORATOR_TITLE_NAME, ((Text)decoratortitle.getFirstChild()).getContent());
					pagetitle.setAllNodeLocalVariables(decoratortitlemap);

					// Let the content pattern override the decorator pattern
					Attribute contenttitlepattern = pagetitle.getAttributeMap().get(PROCESSOR_NAME_TITLEPATTERN_FULL);
					pullAttributes(pagetitle, decoratortitle);
					if (contenttitlepattern != null) {
						pagetitle.setAttribute(PROCESSOR_NAME_TITLEPATTERN_FULL, contenttitlepattern.getValue());
					}
				}
			}
			else {
				decoratorhead.insertChild(0, new Text(LINE_SEPARATOR));
				decoratorhead.insertChild(1, pagetitle);
			}
		}
		for (Node pageheadnode: pagehead.getChildren()) {
			decoratorhead.addChild(pageheadnode);
		}

		pullAttributes(decoratorhead, pagehead);
	}
}
