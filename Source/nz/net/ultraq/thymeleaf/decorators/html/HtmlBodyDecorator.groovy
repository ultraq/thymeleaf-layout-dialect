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

import org.thymeleaf.model.IModel

/**
 * A decorator specific to processing an HTML BODY element.
 * 
 * @author Emanuel Rabina
 */
class HtmlBodyDecorator extends XmlElementDecorator {

	/**
	 * Decorate the BODY part.  This step merges the decorator and content BODY
	 * attributes, ensuring only that a BODY element actually exists in the
	 * result.  The bulk of the body decoration is actually performed by the
	 * fragment replacements.
	 * 
	 * @param decoratorHtml Decorator's HTML element.
	 * @param contentBody	Content's BODY element.
	 */
	@Override
	void decorate(IModel decoratorHtml, IModel contentBody) {

		// If the page has no BODY, then we don't need to do anything
/*		if (!contentBody) {
			return
		}

		// If the decorator has no BODY, we can just copy the page BODY
		def decoratorBody = decoratorHtml.findElement('body')
		if (!decoratorBody) {
			decoratorHtml.addChild(contentBody)
			decoratorHtml.addChild(new Text(System.properties.'line.separator'))
			return
		}

		super.decorate(decoratorBody, contentBody)
*/	}
}
