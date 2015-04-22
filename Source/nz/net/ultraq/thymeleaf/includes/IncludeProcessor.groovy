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

package nz.net.ultraq.thymeleaf.includes

import nz.net.ultraq.thymeleaf.fragments.FragmentLocator
import nz.net.ultraq.thymeleaf.fragments.FragmentMap
import static nz.net.ultraq.thymeleaf.fragments.FragmentProcessor.PROCESSOR_NAME_FRAGMENT
import static nz.net.ultraq.thymeleaf.LayoutDialect.DIALECT_PREFIX_LAYOUT

import org.thymeleaf.Arguments
import org.thymeleaf.dom.Element
import org.thymeleaf.processor.ProcessorResult
import org.thymeleaf.processor.attr.AbstractAttrProcessor
import org.thymeleaf.standard.fragment.StandardFragmentProcessor

/**
 * Similar to Thymeleaf's <tt>th:include</tt>, but allows the passing of entire
 * element fragments to the included template.  Useful if you have some HTML
 * that you want to reuse, but whose contents are too complex to determine or
 * construct with context variables alone.
 * 
 * @author Emanuel Rabina
 */
class IncludeProcessor extends AbstractAttrProcessor {

	static final String PROCESSOR_NAME_INCLUDE = 'include'

	final int precedence = 0

	/**
	 * Constructor, sets this processor to work on the 'include' attribute.
	 */
	IncludeProcessor() {

		super(PROCESSOR_NAME_INCLUDE)
	}

	/**
	 * Locates the specified page and includes it into the current template.
	 * 
	 * @param arguments
	 * @param element
	 * @param attributeName
	 * @return Result of the processing.
	 */
	@Override
	protected ProcessorResult processAttribute(Arguments arguments, Element element, String attributeName) {

		// Locate the page and fragment to include
		def fragment = StandardFragmentProcessor.computeStandardFragmentSpec(
				arguments.configuration, arguments, element.getAttributeValue(attributeName),
				DIALECT_PREFIX_LAYOUT, PROCESSOR_NAME_FRAGMENT)
		def includeFragments = fragment.extractFragment(arguments.configuration,
				arguments, arguments.templateRepository)

		// Gather all fragment parts within the include element
		FragmentMap.fragmentMapForContext(arguments.context).putAll(
			new FragmentLocator(element.elementChildren).locate()
		)

		// Replace the children of this element with those of the include page fragments
		element.clearChildren()
		if (includeFragments) {
			def containerElement = new Element('container')
			includeFragments.each({ includeFragment ->
				containerElement.addChild(includeFragment)
				containerElement.extractChild(includeFragment)
			})
			containerElement.children.each({ extractedChild ->
				element.addChild(extractedChild)
			})
		}

		element.removeAttribute(attributeName)
		return ProcessorResult.OK
	}
}
