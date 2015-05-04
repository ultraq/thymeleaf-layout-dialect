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

import groovy.transform.InheritConstructors

/**
 * Merges an element and all its children into an existing element.
 * 
 * @author Emanuel Rabina
 */
@InheritConstructors
class ElementMerger extends AttributeMerger {

	/**
	 * Constructor, configures the merger.
	 */
	ElementMerger() {

		super(true, false)
	}

	/**
	 * Replace the content of the target element, with the content of the source
	 * element.
	 * 
	 * @param targetElement
	 * @param sourceElement
	 */
	@Override
	void merge(Element targetElement, Element sourceElement) {

		// NOTE: Not really fond of how I have to merge from target to source
		//       with an internal configuration option, but it's the only way I
		//       know how given that the target gets replaced via the
		//       extractChild() process
		super.merge(sourceElement, targetElement)

		targetElement.clearChildren()
		targetElement.addChild(sourceElement.cloneNode(null, false))
		targetElement.parent.extractChild(targetElement)
	}
}
