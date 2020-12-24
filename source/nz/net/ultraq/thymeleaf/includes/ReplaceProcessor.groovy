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

import nz.net.ultraq.thymeleaf.expressions.ExpressionProcessor
import nz.net.ultraq.thymeleaf.fragments.FragmentFinder
import nz.net.ultraq.thymeleaf.fragments.FragmentMap
import nz.net.ultraq.thymeleaf.fragments.FragmentParameterVariableUpdater
import nz.net.ultraq.thymeleaf.models.TemplateModelFinder

import org.thymeleaf.context.ITemplateContext
import org.thymeleaf.engine.AttributeName
import org.thymeleaf.model.IModel
import org.thymeleaf.processor.element.AbstractAttributeModelProcessor
import org.thymeleaf.processor.element.IElementModelStructureHandler
import org.thymeleaf.templatemode.TemplateMode

/**
 * Similar to Thymeleaf's {@code th:replace}, but allows the passing of entire
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
	 * Locates a page fragment and uses it to replace the current element.
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

		// Locate the page and fragment to use for replacement
		def fragmentExpression = new ExpressionProcessor(context).parseFragmentExpression(attributeValue)
		def fragmentForReplacement = new TemplateModelFinder(context).findFragment(fragmentExpression)

		// Gather all fragment parts within the include element, scoping them to this element
		def includeFragments = new FragmentFinder(dialectPrefix).findFragments(model)
		FragmentMap.setForNode(context, structureHandler, includeFragments, true)

		// Keep track of what template is being processed?  Thymeleaf does this for
		// its include processor, so I'm just doing the same here.
		structureHandler.templateData = fragmentForReplacement.templateData

		// Replace this element with the located fragment
		def fragmentForReplacementUse = fragmentForReplacement.cloneModel()
		model.replaceModel(0, fragmentForReplacementUse)

		// Scope variables in fragment definition to current fragment
		new FragmentParameterVariableUpdater(dialectPrefix, context)
			.updateLocalVariables(fragmentExpression, fragmentForReplacementUse, structureHandler)
	}
}
