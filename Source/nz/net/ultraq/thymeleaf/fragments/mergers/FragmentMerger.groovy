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

/**
 * Merges page fragments by bringing in source content into a target element.
 * 
 * @author Emanuel Rabina
 */
interface FragmentMerger {

	/**
	 * Merge both the attributes and content of the source element into the
	 * target element.
	 * 
	 * @param targetElement
	 * @param sourceElement
	 */
	void merge(Element targetElement, Element sourceElement)
}
