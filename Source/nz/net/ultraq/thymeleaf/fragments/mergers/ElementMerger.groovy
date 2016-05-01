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

import org.thymeleaf.model.IModel

import groovy.transform.TupleConstructor

/**
 * Merges an element and all its children into an existing element.
 * 
 * @author Emanuel Rabina
 */
@TupleConstructor
class ElementMerger extends AttributeMerger {

	/**
	 * Flag for indicating that the merge is over a root element, in which some
	 * special rules apply.
	 */
	final boolean rootElementMerge

	/**
	 * Replace the content of the target element, with the content of the source
	 * element.
	 * 
	 * @param targetElement
	 * @param sourceElement
	 */
	@Override
	void merge(IModel targetElement, IModel sourceElement) {

		// Create a new merged element to mess with
/*		def mergedElement = sourceElement.cloneNode(null, false)
		if (!rootElementMerge) {
			mergedElement.clearAttributes()
			targetElement.attributeMap.values().each { attribute ->
				mergedElement.setAttribute(attribute.normalizedName, attribute.value)
			}
			super.merge(mergedElement, sourceElement)
		}
		targetElement.clearChildren()
		targetElement.addChild(mergedElement)
		targetElement.parent.extractChild(targetElement)
*/	}
}
