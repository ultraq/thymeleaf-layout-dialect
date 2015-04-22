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

import nz.net.ultraq.thymeleaf.decorators.xml.XmlDocumentDecorator

import org.thymeleaf.dom.Element

/**
 * A decorator made to work over whole HTML pages.  Decoration will be done in
 * 2 phases: a special one for the HEAD element, and a generic one for the BODY
 * element.
 * 
 * @author Emanuel Rabina
 */
class HtmlDocumentDecorator extends XmlDocumentDecorator {

	/**
	 * Decorate an entire HTML page.
	 * 
	 * @param decoratorHtml Decorator's HTML element.
	 * @param contentHtml	Content's HTML element.
	 */
	@Override
	public void decorate(Element decoratorHtml, Element contentHtml) {

		new HtmlHeadDecorator().decorate(decoratorHtml, contentHtml.findElement('head'))
		new HtmlBodyDecorator().decorate(decoratorHtml, contentHtml.findElement('body'))

		// Set the doctype from the decorator if missing from the content page
		def decoratorDocument = decoratorHtml.parent
		def contentDocument   = contentHtml.parent
		if (!contentDocument.docType && decoratorDocument.docType) {
			contentDocument.docType = decoratorDocument.docType
		}

		super.decorate(decoratorHtml, contentHtml)
	}
}
