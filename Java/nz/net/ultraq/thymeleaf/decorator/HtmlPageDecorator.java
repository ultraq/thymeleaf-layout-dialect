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

import org.thymeleaf.dom.Document;
import org.thymeleaf.dom.Element;

/**
 * A decorator made to work over whole HTML pages.  Decoration will be done in
 * 2 phases: a special one for the &lt;head&gt; element, and a generic one for
 * the &lt;body&gt; element.
 * 
 * @author Emanuel Rabina
 */
public class HtmlPageDecorator extends Decorator {

	private final HtmlHeadDecorator headdecorator = new HtmlHeadDecorator();
	private final HtmlBodyDecorator bodydecorator = new HtmlBodyDecorator();

	/**
	 * Decorate an entire HTML page.
	 * 
	 * @param decoratorhtml Decorator's HTML element.
	 * @param pagehtml		Page's HTML element.
	 */
	@Override
	public void decorate(Element decoratorhtml, Element pagehtml) {

		headdecorator.decorate(decoratorhtml, findElement(pagehtml, HTML_ELEMENT_HEAD));
		bodydecorator.decorate(decoratorhtml, findElement(pagehtml, HTML_ELEMENT_BODY));

		// Pull the decorator page into this document
		if (pagehtml.getOriginalName().equals(HTML_ELEMENT_HTML)) {
			pullAttributes(decoratorhtml, pagehtml);
		}
		Document decoratordocument = (Document)decoratorhtml.getParent();
		Document pagedocument = (Document)pagehtml.getParent();
		if (decoratordocument.hasDocType() && !pagedocument.hasDocType()) {
			pagedocument.setDocType(decoratordocument.getDocType());
		}

		super.decorate(decoratorhtml, pagehtml);
	}
}
