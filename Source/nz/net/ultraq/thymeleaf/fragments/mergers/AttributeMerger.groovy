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

import static nz.net.ultraq.thymeleaf.LayoutDialect.DIALECT_PREFIX_LAYOUT
import static nz.net.ultraq.thymeleaf.fragments.FragmentProcessor.PROCESSOR_NAME_FRAGMENT

import org.thymeleaf.dom.Element
import org.thymeleaf.standard.StandardDialect
import org.thymeleaf.standard.processor.attr.StandardWithAttrProcessor

import groovy.transform.TupleConstructor

/**
 * Merges a source element's attributes into a target element.
 * 
 * @author Emanuel Rabina
 */
@TupleConstructor
class AttributeMerger implements FragmentMerger {

	/**
	 * Set to <tt>true</tt> to pull only attributes that exist in
	 * <tt>targetelement</tt>.  <tt>th:with</tt> values will continue to be
	 * brought in regardless.
	 */
	final boolean mergeOnly

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

		// Exclude the copying of fragment attributes
		def sourceAttributes = sourceElement.attributeMap.values().findAll { sourceAttribute ->
			return !sourceAttribute.equalsName(DIALECT_PREFIX_LAYOUT, PROCESSOR_NAME_FRAGMENT)
		}

		sourceAttributes.each { sourceAttribute ->

			// Merge th:with attributes to retain local variable declarations
			if (isWithAttribute(sourceAttribute)) {
				withAttributeMerger(sourceAttribute)
			}

			// Copy only attributes that already exist in the target element, or
			// copy any attributes
			else if ((mergeOnly && isNewAttribute(sourceAttribute)) || !mergeOnly) {
				attributeMerger(sourceAttribute)
			}
		}
	}
}
