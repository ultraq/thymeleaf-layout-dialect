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

import nz.net.ultraq.thymeleaf.expressions.ExpressionProcessor
import nz.net.ultraq.thymeleaf.fragments.FragmentFinder
import nz.net.ultraq.thymeleaf.fragments.FragmentMap
import nz.net.ultraq.thymeleaf.models.TemplateModelFinder

import org.slf4j.Logger
import org.slf4j.LoggerFactory
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
 * @deprecated Use {@link InsertProcessor} ({@code layout:insert}) instead.
 */
@Deprecated
class IncludeProcessor extends AbstractAttributeModelProcessor {

	private static final Logger logger = LoggerFactory.getLogger(IncludeProcessor)

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

		logger.warn('''
			The layout:include/data-layout-include processor is deprecated and will be
			removed in the next major version of the layout dialect.  Use the
			layout:insert/data-layout-insert processor instead.
		'''.stripIndent().trim())

		// Locate the page and fragment for inclusion
		def fragmentExpression = new ExpressionProcessor(context).parse(attributeValue)
		def fragmentForInclusion = new TemplateModelFinder(context, templateMode).findFragment(
			fragmentExpression.templateName.toString(), fragmentExpression.fragmentSelector.toString(),
			dialectPrefix)

		// Gather all fragment parts within the include element, scoping them to this element
		def includeFragments = new FragmentFinder(dialectPrefix).findFragments(model)
		FragmentMap.setForNode(context, structureHandler, includeFragments);

		// Keep track of what template is being processed?  Thymeleaf does this for
		// its include processor, so I'm just doing the same here.
		structureHandler.templateData = fragmentForInclusion.templateData

		// Replace the children of this element with the children of the included page fragment
		model.clearChildren()
		fragmentForInclusion.cloneModel().childEventIterator().each { fragmentChildEvent ->
			model.insert(model.size() - 1, fragmentChildEvent)
		}
	}
}
