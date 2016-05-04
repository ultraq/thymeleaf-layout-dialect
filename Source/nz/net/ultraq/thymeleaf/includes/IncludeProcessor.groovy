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

import org.thymeleaf.context.ITemplateContext
import org.thymeleaf.engine.AttributeName
import org.thymeleaf.model.IModel
import org.thymeleaf.processor.element.AbstractAttributeModelProcessor
import org.thymeleaf.processor.element.IElementModelStructureHandler
import org.thymeleaf.templatemode.TemplateMode

/**
 * Similar to Thymeleaf's {@code th:include}, but allows the passing of entire
 * element fragments to the included template.  Useful if you have some HTML
 * that you want to reuse, but whose contents are too complex to determine or
 * construct with context variables alone.
 * 
 * @author Emanuel Rabina
 */
class IncludeProcessor extends AbstractAttributeModelProcessor {

	static final String PROCESSOR_NAME = 'include'
	static final int PROCESSOR_PRECEDENCE = 0

	/**
	 * Constructor, sets this processor to work on the 'include' attribute.
	 * 
	 * @param templateMode
	 * @param dialectPrefix
	 */
	IncludeProcessor(TemplateMode templateMode, String dialectPrefix) {

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
/*		def includeFragments = new FragmentFinder(arguments)
				.findFragments(element.getAttributeValue(attributeName))

		// Gather all fragment parts within the include element, scoping them to
		// this element
		def elementFragments = new FragmentMapper().map(element.elementChildren)
		FragmentMap.setForNode(arguments, element, elementFragments)

		// Replace the children of this element with those of the include page
		// fragments.  The 'container' element is copied from how Thymeleaf does
		// it's include processor, which is to maintain internal structures like
		// local variables
		element.clearChildren()
		if (includeFragments) {
			def containerElement = new Element('container')
			includeFragments.each { includeFragment ->
				containerElement.addChild(includeFragment)
				containerElement.extractChild(includeFragment)
			}
			containerElement.children.each { extractedChild ->
				element.addChild(extractedChild)
			}
		}

		element.removeAttribute(attributeName)
		return ProcessorResult.OK
*/	}
}
