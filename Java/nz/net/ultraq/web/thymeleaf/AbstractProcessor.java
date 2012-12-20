/*
 * Copyright 2012, Emanuel Rabina (http://www.ultraq.net.nz/)
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

package nz.net.ultraq.web.thymeleaf;

import org.thymeleaf.dom.Attribute;
import org.thymeleaf.dom.Element;
import org.thymeleaf.processor.attr.AbstractAttrProcessor;
import org.thymeleaf.standard.StandardDialect;
import org.thymeleaf.standard.processor.attr.StandardWithAttrProcessor;

/**
 * Common code for the layout processors.
 * 
 * @author Emanuel Rabina
 */
public abstract class AbstractProcessor extends AbstractAttrProcessor {

	private static final String TH_WITH = StandardDialect.PREFIX + ":" + StandardWithAttrProcessor.ATTR_NAME;

	/**
	 * Subclass constructor, set the attribute name that this processor will
	 * respond to.
	 * 
	 * @param attribute
	 */
	protected AbstractProcessor(String attribute) {

		super(attribute);
	}

	/**
	 * Merge the content element attributes with those of the target element
	 * 
	 * @param contentelement
	 * @param targetelement
	 */
	protected static void mergeAttributes(Element contentelement, Element targetelement) {

		if (contentelement == null || targetelement == null) {
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
	 * Pull the attributes and child contents of the target element into the
	 * content element, merging attributes as necessary (content attributes take
	 * precedence).
	 * 
	 * @param contentelement
	 * @param targetelement
	 */
	protected static void pullTargetContent(Element contentelement, Element targetelement) {

		// Clone target element without processing information to make Thymeleaf reprocesses it
		contentelement.clearChildren();
		contentelement.addChild(targetelement.cloneNode(null, false));
		contentelement.getParent().extractChild(contentelement);
	}
}
