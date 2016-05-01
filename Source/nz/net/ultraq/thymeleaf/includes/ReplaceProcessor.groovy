/* 
 * Copyright 2013, Emanuel Rabina (http://www.ultraq.net.nz/)
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

import nz.net.ultraq.thymeleaf.fragments.FragmentFinder
import nz.net.ultraq.thymeleaf.fragments.FragmentMap
import nz.net.ultraq.thymeleaf.fragments.FragmentMapper

import org.thymeleaf.context.ITemplateContext
import org.thymeleaf.engine.AttributeName
import org.thymeleaf.model.IModel
import org.thymeleaf.processor.element.AbstractAttributeModelProcessor
import org.thymeleaf.processor.element.IElementModelStructureHandler
import org.thymeleaf.templatemode.TemplateMode

/**
 * Similar to Thymeleaf's <tt>th:replace</tt>, but allows the passing of entire
 * element fragments to the included template.  Useful if you have some HTML
 * that you want to reuse, but whose contents are too complex to determine or
 * construct with context variables alone.
 * 
 * @author Emanuel Rabina
 */
class ReplaceProcessor extends AbstractAttributeModelProcessor {

	static final String PROCESSOR_NAME = 'replace'
	static final int PROCESSOR_PRECEDENCE = 0

	/**
	 * Constructor, set this processor to work on the 'replace' attribute.
	 * 
	 * @param templateMode
	 * @param dialectPrefix
	 */
	ReplaceProcessor(TemplateMode templateMode, String dialectPrefix) {

		super(templateMode, dialectPrefix, null, false, PROCESSOR_NAME, true, PROCESSOR_PRECEDENCE, true)
	}

	/**
	 * Locates a page fragment and includes it in the current template.
	 * 
	 * @param context
	 * @param model
	 * @param attributeName
	 * @param attributeValue
	 * @param structureHandler
	 */
	@Override
	protected void doProcess(ITemplateContext context, IModel model, AttributeName attributeName,
		String attributeValue, IElementModelStructureHandler structureHandler) {

		// Locate the page and fragment to include
/*		def replaceFragments = new FragmentFinder(arguments)
				.findFragments(element.getAttributeValue(attributeName))

		// Gather all fragment parts within the replace element
		def elementFragments = new FragmentMapper().map(element.elementChildren)

		// Replace the children of this element with those of the replace page
		// fragments, scoping any fragment parts to the immediate children
		element.clearChildren()
		if (replaceFragments) {
			replaceFragments.each { replaceFragment ->
				element.addChild(replaceFragment)
				FragmentMap.setForNode(arguments, replaceFragment, elementFragments)
			}
		}
		element.parent.extractChild(element)

		element.removeAttribute(attributeName)
		return ProcessorResult.OK
*/	}
}
