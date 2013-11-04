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

package nz.net.ultraq.thymeleaf;

import org.thymeleaf.dom.Attribute;
import org.thymeleaf.dom.Element;
import org.thymeleaf.standard.StandardDialect;
import org.thymeleaf.standard.processor.attr.StandardTextAttrProcessor;
import org.thymeleaf.standard.processor.attr.StandardWithAttrProcessor;

/**
 * Common decorator-specific utility methods.
 * 
 * @author Emanuel Rabina
 */
public final class LayoutUtilities {

	public static final String LINE_SEPARATOR     = System.getProperty("line.separator");
	public static final String HTML_ELEMENT_HTML  = "html";
	public static final String HTML_ELEMENT_HEAD  = "head";
	public static final String HTML_ELEMENT_TITLE = "title";
	public static final String HTML_ELEMENT_BODY  = "body";

	/**
	 * Default hidden constructor as this class is only ever meant to be used
	 * statically.
	 */
	private LayoutUtilities() {
	}

	/**
	 * Returns whether the attribute contains the given prefix and name parts.
	 * Checks both the prefixed and data- versions of a Thymeleaf attribute
	 * processor.
	 * 
	 * @param attribute
	 * @param prefix
	 * @param name
	 * @return <tt>true</tt> if the attribute name consists of the prefix and
	 *         name parts.
	 */
	public static boolean equalsAttributeName(Attribute attribute, String prefix, String name) {

		String attributename = attribute.getOriginalName();
		return attributename.equals(prefix + ":" + name) ||
		       attributename.equals("data-" + prefix + "-" + name);
	}

	/**
	 * Returns the value of the attribute.  Checks both the prefixed and data-
	 * versions of a Thymeleaf attribute processor.
	 * 
	 * @param element
	 * @param prefix
	 * @param name
	 * @return The value of the attribute, in either form, or <tt>null</tt> if
	 *         the value doesn't exist in either form.
	 */
	public static String getAttributeValue(Element element, String prefix, String name) {

		String value = element.getAttributeValue(prefix + ":" + name);
		if (value == null) {
			value = element.getAttributeValue("data-" + prefix + "-" + name);
		}
		return value;
	}

	/**
	 * Returns whether or not the attribute on the element exists.  Checks both
	 * the prefixed and data- versions of a Thymeleaf attribute processor.
	 * 
	 * @param element
	 * @param prefix
	 * @param name
	 * @return <tt>true</tt> if the attribute exists on the element, in either
	 *         form.
	 */
	public static boolean hasAttribute(Element element, String prefix, String name) {

		return element.hasAttribute(prefix + ":" + name) ||
		       element.hasAttribute("data-" + prefix + "-" + name);
	}

	/**
	 * Replace the attributes of the source element with those of the target
	 * element.  A merge is done on the <tt>th:with</tt> attribute, while all
	 * other attributes are simply overidden.
	 * 
	 * @param targetelement
	 * @param sourceelement
	 */
	public static void pullAttributes(Element targetelement, Element sourceelement) {

		if (targetelement == null || sourceelement == null) {
			return;
		}

		for (Attribute sourceattribute: sourceelement.getAttributeMap().values()) {

			// Merge th:with attributes to retain local variable declarations
			if (equalsAttributeName(sourceattribute, StandardDialect.PREFIX, StandardWithAttrProcessor.ATTR_NAME)) {
				String targetwithvalue = getAttributeValue(targetelement,
						StandardDialect.PREFIX, StandardTextAttrProcessor.ATTR_NAME);
				if (targetwithvalue != null) {
					targetelement.setAttribute(StandardDialect.PREFIX + ":" + StandardWithAttrProcessor.ATTR_NAME,
							sourceattribute.getValue() + "," + targetwithvalue);
					continue;
				}
			}
			targetelement.setAttribute(sourceattribute.getOriginalName(), sourceattribute.getValue());
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

	/**
	 * Removes an attribute from an element.  Removes both the prefixed and
	 * data- variations of the attribute.
	 * 
	 * @param element
	 * @param prefix
	 * @param name
	 */
	public static void removeAttribute(Element element, String prefix, String name) {

		element.removeAttribute(prefix + ":" + name);
		element.removeAttribute("data-" + prefix + "-" + name);
	}
}
