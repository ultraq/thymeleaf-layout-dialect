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

import static nz.net.ultraq.thymeleaf.LayoutDialect.DIALECT_PREFIX_LAYOUT
import static nz.net.ultraq.thymeleaf.fragments.FragmentProcessor.PROCESSOR_NAME_FRAGMENT
import static nz.net.ultraq.thymeleaf.includes.IncludeProcessor.PROCESSOR_NAME_INCLUDE
import static nz.net.ultraq.thymeleaf.includes.ReplaceProcessor.PROCESSOR_NAME_REPLACE

import org.thymeleaf.dom.Element

/**
 * Searches for and returns fragments in all descendents of an element.
 * 
 * @author Emanuel Rabina
 */
class FragmentLocator {

	/**
	 * Find and return clones of all fragments within the given elements without
	 * delving into <tt>layout:include</tt> or <tt>layout:replace</tt> elements.
	 * 
	 * @param elements List of elements to search.
	 * @return Map of fragment names and their elements.
	 */
	Map<String,Element> locate(List<Element> elements) {

		def fragments = new HashMap<String,Element>()
		def search
		elements.each(search = { element ->
			def fragmentName = element.getAttributeValue(DIALECT_PREFIX_LAYOUT, PROCESSOR_NAME_FRAGMENT)
			if (fragmentName) {
				def fragment = element.cloneNode(null, true)
				element.removeAttribute(DIALECT_PREFIX_LAYOUT, PROCESSOR_NAME_FRAGMENT)
				fragments << [(fragmentName): fragment]
			}
			else if (!element.hasAttribute(DIALECT_PREFIX_LAYOUT, PROCESSOR_NAME_INCLUDE) ||
				!element.hasAttribute(DIALECT_PREFIX_LAYOUT, PROCESSOR_NAME_REPLACE)) {
				search.trampoline(element.elementChildren)
			}
		})
	}
}
