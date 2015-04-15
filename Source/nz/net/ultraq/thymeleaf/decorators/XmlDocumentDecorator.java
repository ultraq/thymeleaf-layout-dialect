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

import org.thymeleaf.dom.Comment;
import org.thymeleaf.dom.Document;
import org.thymeleaf.dom.Element;
import org.thymeleaf.dom.Node;

/**
 * A decorator made to work over any Thymeleaf document.
 * 
 * @author Emanuel Rabina
 */
public class XmlDocumentDecorator extends Decorator {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void decorate(Element decorator, Element content) {

		Document decoratordocument = (Document)decorator.getParent();
		Document contentdocument   = (Document)content.getParent();

		// Copy text outside of the root element, keeping whitespace copied to a minimum
		boolean beforehtml = true;
		boolean allownext = false;
		Node lastnode = content;
		for (Node externalnode: decoratordocument.getChildren()) {
			if (externalnode.equals(decorator)) {
				beforehtml = false;
				allownext = true;
				continue;
			}
			if (externalnode instanceof Comment || allownext) {
				if (beforehtml) {
					contentdocument.insertBefore(content, externalnode);
				}
				else {
					contentdocument.insertAfter(lastnode, externalnode);
					lastnode = externalnode;
				}
				allownext = externalnode instanceof Comment;
			}
		}

		// Bring the decorator into the content page (which is the one being processed)
		pullAttributes(decorator, content, !decorator.getNormalizedName().equals(content.getNormalizedName()));
		pullContent(content, decorator);
	}
}
