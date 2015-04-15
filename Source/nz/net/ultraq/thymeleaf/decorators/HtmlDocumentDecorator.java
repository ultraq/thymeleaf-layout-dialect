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

package nz.net.ultraq.thymeleaf.decorators;

import static nz.net.ultraq.thymeleaf.utilities.LayoutUtilities.*;

import org.thymeleaf.dom.Document;
import org.thymeleaf.dom.Element;

/**
 * A decorator made to work over whole HTML pages.  Decoration will be done in
 * 2 phases: a special one for the HEAD element, and a generic one for the BODY
 * element.
 * 
 * @author Emanuel Rabina
 */
public class HtmlDocumentDecorator extends XmlDocumentDecorator {

	private final HtmlHeadDecorator headdecorator = new HtmlHeadDecorator();
	private final HtmlBodyDecorator bodydecorator = new HtmlBodyDecorator();

	/**
	 * Decorate an entire HTML page.
	 * 
	 * @param decoratorhtml Decorator's HTML element.
	 * @param contenthtml	Content's HTML element.
	 */
	@Override
	public void decorate(Element decoratorhtml, Element contenthtml) {

		headdecorator.decorate(decoratorhtml, findElement(contenthtml, HTML_ELEMENT_HEAD));
		bodydecorator.decorate(decoratorhtml, findElement(contenthtml, HTML_ELEMENT_BODY));

		// Set the doctype from the decorator if missing from the content page
		Document decoratordocument = (Document)decoratorhtml.getParent();
		Document pagedocument = (Document)contenthtml.getParent();
		if (decoratordocument.hasDocType() && !pagedocument.hasDocType()) {
			pagedocument.setDocType(decoratordocument.getDocType());
		}

		super.decorate(decoratorhtml, contenthtml);
	}
}
