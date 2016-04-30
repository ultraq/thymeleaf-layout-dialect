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

import org.thymeleaf.context.ITemplateContext
import org.thymeleaf.engine.TemplateModel
import org.thymeleaf.standard.expression.Fragment
import org.thymeleaf.standard.expression.FragmentExpression
import org.thymeleaf.standard.expression.StandardExpressionExecutionContext
import org.thymeleaf.standard.expression.StandardExpressions
import org.thymeleaf.templatemode.TemplateMode

import groovy.transform.TupleConstructor

/**
 * Hides all the scaffolding business required to find fragments or fragment
 * templates.
 * 
 * @author Emanuel Rabina
 */
@TupleConstructor
class FragmentFinder {

	final ITemplateContext context

	/**
	 * Computes the fragment for the given fragment spec, returning a Thymeleaf
	 * fragment expression object that can be used to further resolve fragments or
	 * templates.
	 * 
	 * @param fragmentSpec
	 * @return Thymeleaf fragment expression object.
	 */
	private FragmentExpression.ExecutedFragmentExpression computeFragment(String fragmentSpec) {

		return FragmentExpression.createExecutedFragmentExpression(context,
			StandardExpressions.getExpressionParser(context.configuration).parseExpression(context,
				fragmentSpec.matches(~/~{.+}/) ? fragmentSpec : "~{${fragmentSpec}}"),
			StandardExpressionExecutionContext.NORMAL)
	}

	/**
	 * Returns the fragment(s) specified by the given fragment spec string.
	 * 
	 * @param fragmentSpec
	 * @return List of fragment nodes matching the fragment specification.
	 */
	Fragment findFragment(String fragmentSpec) {

		return FragmentExpression.resolveExecutedFragmentExpression(context,
			computeFragment(fragmentSpec), true)
	}

	/**
	 * Return the template model specified by the given fragment spec string.
	 * 
	 * @param fragmentSpec
	 * @param templateMode
	 * @return Template model matching the fragment specification.
	 */
	TemplateModel findFragmentTemplateModel(String fragmentSpec, TemplateMode templateMode) {

		return context.configuration.templateManager.parseStandalone(context,
			FragmentExpression.resolveTemplateName(computeFragment(fragmentSpec)),
			null, templateMode, true, true)
	}
}
