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

import nz.net.ultraq.thymeleaf.fragments.FragmentMerger
import nz.net.ultraq.thymeleaf.fragments.FragmentProcessor
import static nz.net.ultraq.thymeleaf.LayoutDialect.DIALECT_PREFIX

import org.thymeleaf.model.IModel
import org.thymeleaf.standard.StandardDialect

/**
 * Merges attributes from one element into another.
 * 
 * @author Emanuel Rabina
 */
class AttributeMerger implements FragmentMerger {

	/**
	 * Merge the attributes of the source element with those of the target
	 * element.  In most cases, this means overwriting the target attributes with
	 * the source ones, except for some special merging on <tt>th:with</tt>
	 * attributes so that variable declarations are preserved.
	 * 
	 * @param sourceElement
	 * @param targetElement
	 */
	@Override
	void merge(IModel targetElement, IModel sourceElement) {

		if (!sourceElement || !targetElement) {
			return
		}

		// Exclude the copying of fragment attributes
		def sourceAttributes = sourceElement.attributeMap.values().findAll { sourceAttribute ->
			return !sourceAttribute.equalsName(DIALECT_PREFIX_LAYOUT, PROCESSOR_NAME_FRAGMENT)
		}

		sourceAttributes.each { sourceAttribute ->

			// Merge th:with attributes to retain local variable declarations
			if (sourceAttribute.equalsName(StandardDialect.PREFIX, StandardWithAttrProcessor.ATTR_NAME)) {
				def mergedWithValue = sourceAttribute.value
				def targetWithValue = targetElement.getAttributeValue(StandardDialect.PREFIX, StandardWithAttrProcessor.ATTR_NAME)
				if (targetWithValue) {
					mergedWithValue += ",${targetWithValue}"
				}
				targetElement.setAttribute("${StandardDialect.PREFIX}:${StandardWithAttrProcessor.ATTR_NAME}", mergedWithValue)
			}

			// Copy every other attribute straight
			else {
				targetElement.setAttribute(sourceAttribute.originalName, sourceAttribute.value)
			}
		}
	}
}
