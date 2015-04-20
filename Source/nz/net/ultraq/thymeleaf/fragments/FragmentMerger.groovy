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

package nz.net.ultraq.thymeleaf.fragments

import org.thymeleaf.dom.Element
import org.thymeleaf.standard.StandardDialect
import org.thymeleaf.standard.processor.attr.StandardWithAttrProcessor

import groovy.transform.Immutable

/**
 * Merges page fragments by bringing in source HTML content and attributes into
 * a target element.
 * 
 * @author Emanuel Rabina
 */
@Immutable
class FragmentMerger {

	private final Element targetElement
	private final Element sourceElement

	/**
	 * Merge both the attributes and content of the source element into the
	 * target element.
	 * 
	 * @param mergeExisting Set to <tt>true</tt> to bring only attributes that
	 *                  already exist in the target element.
	 */
	void merge(boolean mergeExisting = false) {

		mergeAttributes(targetElement, sourceElement, mergeExisting)
		mergeElements(targetElement, sourceElement)
	}

	/**
	 * Merges attributes between elements, with the option to specify whether to
	 * copy everything over, or only attributes that already exist in the source
	 * element.
	 * 
	 * @param mergeExisting <tt>true</tt> to pull only attributes that exist in
	 *                      <tt>targetelement</tt>.  <tt>th:with</tt> values
	 *                      will continue to be brought in regardless.
	 */
	void mergeAttributes(boolean mergeExisting = false) {

		if (!sourceElement || !targetElement) {
			return
		}

		sourceElement.attributeMap.values().forEach { sourceAttribute ->

			// Merge th:with attributes to retain local variable declarations
			if (sourceAttribute.equalsName(StandardDialect.PREFIX, StandardWithAttrProcessor.ATTR_NAME)) {
				String mergedWithValue = sourceAttribute.value
				String targetWithValue = targetElement.getAttributeValue(StandardDialect.PREFIX, StandardWithAttrProcessor.ATTR_NAME)
				if (targetWithValue) {
					mergedWithValue += ",${targetWithValue}"
				}
				targetElement.setAttribute("${StandardDialect.PREFIX}:${StandardWithAttrProcessor.ATTR_NAME}", mergedWithValue)
			}

			// Copy only attributes that already exist in the target element, or
			// copy any attributes
			else if ((mergeExisting && targetElement.hasAttribute(sourceAttribute.normalizedName)) ||
				!mergeExisting) {
				targetElement.setAttribute(sourceAttribute.originalName, sourceAttribute.value)
			}
		}
	}

	/**
	 * Replace the content of the target element, with the content of the source
	 * element.
	 */
	void mergeElements() {

		// Clone target element without processing information to make Thymeleaf reprocesses it
		targetElement.clearChildren()
		targetElement.addChild(sourceElement.cloneNode(null, false))
		targetElement.parent.extractChild(targetElement)
	}
}
