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

import org.thymeleaf.dom.Attribute;
import org.thymeleaf.dom.Element;
import org.thymeleaf.standard.StandardDialect;
import org.thymeleaf.standard.processor.attr.StandardWithAttrProcessor;

/**
 * Common decorator-specific utility methods.
 * 
 * @author Emanuel Rabina
 */
public final class DecoratorUtilities {

	private static final String TH_WITH = StandardDialect.PREFIX + ":" + StandardWithAttrProcessor.ATTR_NAME;

	public static final String LINE_SEPARATOR     = System.getProperty("line.separator");
	public static final String HTML_ELEMENT_HTML  = "html";
	public static final String HTML_ELEMENT_HEAD  = "head";
	public static final String HTML_ELEMENT_TITLE = "title";
	public static final String HTML_ELEMENT_BODY  = "body";

	/**
	 * Default hidden constructor as this class is only ever meant to be used
	 * statically.
	 */
	private DecoratorUtilities() {
	}

	/**
	 * Recursive search for an element within the given node in the DOM tree.
	 * 
	 * @param element Node to initiate the search from.
	 * @param name	  Name of the element to look for.
	 * @return Element with the given name, or <tt>null</tt> if the element
	 * 		   could not be found.
	 */
	public static Element findElement(Element element, String name) {

		if (element.getOriginalName().equals(name)) {
			return element;
		}
		for (Element child: element.getElementChildren()) {
			Element result = findElement(child, name);
			if (result != null) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Replace the attributes of the source element with those of the target
	 * element.  A merge is done on the <tt>th:with</tt> attribute, while all
	 * other attributes are simply overidden.
	 * 
	 * @param targetelement
	 * @param contentelement
	 */
	public static void pullAttributes(Element targetelement, Element contentelement) {

		if (targetelement == null || contentelement == null) {
			return;
		}

		for (Attribute contentattribute: contentelement.getAttributeMap().values()) {
			String attributename = contentattribute.getOriginalName();

			// Merge th:with attributes to retain local variable declarations
			if (attributename.equals(TH_WITH) && targetelement.hasAttribute(TH_WITH)) {
				targetelement.setAttribute(attributename, contentattribute.getValue() + "," +
						targetelement.getAttributeValue(TH_WITH));
			}
			else {
				targetelement.setAttribute(contentattribute.getOriginalName(), contentattribute.getValue());
			}
		}
	}

	/**
	 * Replace the target element with the source element.
	 * 
	 * @param targettelement
	 * @param sourceelement
	 */
	public static void pullContent(Element targettelement, Element sourceelement) {

		// Clone target element without processing information to make Thymeleaf reprocesses it
		targettelement.clearChildren();
		targettelement.addChild(sourceelement.cloneNode(null, false));
		targettelement.getParent().extractChild(targettelement);
	}
}
