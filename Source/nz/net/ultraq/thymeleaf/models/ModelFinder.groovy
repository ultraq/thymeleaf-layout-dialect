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

package nz.net.ultraq.thymeleaf.models

import org.thymeleaf.context.ITemplateContext
import org.thymeleaf.engine.TemplateModel
import org.thymeleaf.standard.expression.StandardExpressions
import org.thymeleaf.templatemode.TemplateMode

/**
 * Hides all the scaffolding business required to find fragments.
 * 
 * @author Emanuel Rabina
 */
class ModelFinder {

	final ITemplateContext context
	final TemplateMode templateMode

	/**
	 * Constructor, set the template context we're working in.
	 * 
	 * @param context
	 * @param templateMode
	 */
	ModelFinder(ITemplateContext context, TemplateMode templateMode) {

		this.context      = context
		this.templateMode = templateMode
	}

	/**
	 * Process the expression to resolve the template or fragment name.
	 * 
	 * @param expression
	 * @return Thymeleaf fragment expression object.
	 */
	private String resolveExpression(String expression) {

		return StandardExpressions.getExpressionParser(context.configuration)
			.parseExpression(context, expression)
			.execute(context)
			.toString()
	}

	/**
	 * Return the fragment model specified by the given fragment name expression.
	 * 
	 * @param fragmentNameExpression
	 * @param dialectPrefix
	 * @return Fragment matching the fragment specification.
	 */
	TemplateModel findFragment(String fragmentNameExpression, String dialectPrefix) {

		def fragmentName = resolveExpression(fragmentNameExpression)
		def fragmentSelector = "//[${dialectPrefix}:fragment='${fragmentName}' or data-${dialectPrefix}-fragment='${fragmentName}']"
		return context.configuration.templateManager.parseStandalone(context,
			context.templateData.template, [fragmentSelector.toString()] as Set,
			templateMode, true, true)
	}

	/**
	 * Return the template model specified by the given template name expression.
	 * 
	 * @param templateNameExpression
	 * @return Template model matching the fragment specification.
	 */
	TemplateModel findTemplate(String templateNameExpression) {

		return context.configuration.templateManager.parseStandalone(context,
			resolveExpression(templateNameExpression), null, templateMode, true, true)
	}
}
