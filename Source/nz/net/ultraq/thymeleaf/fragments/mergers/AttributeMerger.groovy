/* 
 * Copyright 2015, Emanuel Rabina (http://www.ultraq.net.nz/)
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

package nz.net.ultraq.thymeleaf.fragments.mergers

import org.thymeleaf.dom.Element
import org.thymeleaf.standard.StandardDialect
import org.thymeleaf.standard.processor.attr.StandardWithAttrProcessor

/**
 * Merges a source element's attributes into a target element.
 * 
 * @author Emanuel Rabina
 */
class AttributeMerger implements FragmentMerger {

	final boolean newAttributesOnly
	final boolean withAttributesOnly

	/**
	 * Constructor, configure the merger.  By default, all attributes get copied
	 * over.  Use the following parameters to limit that copying.
	 * 
	 * @param newAttributesOnly  Set this to <tt>true</tt> to only copy
	 *                           attributes that aren't in the target element.
	 * @param withAttributesOnly Set this to <tt>true</tt> to only copy
	 *                           <tt>th:with</tt>/<tt>data-th-with</tt>
	 *                           attributes.
	 */
	AttributeMerger(boolean newAttributesOnly = false, boolean withAttributesOnly = false) {

		this.newAttributesOnly  = newAttributesOnly
		this.withAttributesOnly = withAttributesOnly
	}

	/**
	 * Merge source element attributes into a target element, overwriting those
	 * attributes found in the target with those from the source.
	 * 
	 * @param sourceElement
	 * @param targetElement
	 */
	@Override
	void merge(Element targetElement, Element sourceElement) {

		if (!sourceElement || !targetElement) {
			return
		}

		def isNewAttribute = { attribute ->
			return !targetElement.hasAttribute(attribute.originalName)
		}
		def isWithAttribute = { attribute ->
			return attribute.equalsName(StandardDialect.PREFIX, StandardWithAttrProcessor.ATTR_NAME)
		}
		def attributeMerger = { attribute ->
			targetElement.setAttribute(attribute.originalName, attribute.value)
		}
		def withAttributeMerger = { withAttribute ->
			def mergedWithValue = withAttribute.value
			def targetWithValue = targetElement.getAttributeValue(StandardDialect.PREFIX, StandardWithAttrProcessor.ATTR_NAME)
			if (targetWithValue) {
				mergedWithValue += ",${targetWithValue}"
			}
			targetElement.setAttribute("${StandardDialect.PREFIX}:${StandardWithAttrProcessor.ATTR_NAME}", mergedWithValue)
		}

		def sourceAttributes = sourceElement.attributeMap.values()

		// For copying only new attributes
		if (newAttributesOnly) {
			sourceAttributes.findAll(isNewAttribute).each(attributeMerger)
		}

		// For copying only th:with attributes
		else if (withAttributesOnly) {
			sourceAttributes.findAll(isWithAttribute).each(withAttributeMerger)
		}

		// For all attributes
		else {
			sourceAttributes.each { sourceAttribute ->
				if (isWithAttribute(sourceAttribute)) {
					withAttributeMerger(sourceAttribute)
				}
				else {
					attributeMerger(sourceAttribute)
				}
			}
		}
	}
}
