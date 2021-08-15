/* 
 * Copyright 2019, Emanuel Rabina (http://www.ultraq.net.nz/)
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

package nz.net.ultraq.thymeleaf.layoutdialect.fragments

import org.thymeleaf.context.ITemplateContext
import org.thymeleaf.model.IModel
import org.thymeleaf.processor.element.IElementModelStructureHandler
import org.thymeleaf.standard.expression.FragmentExpression

import groovy.transform.TupleConstructor

/**
 * Updates the variables at a given element/fragment scope to include those in
 * a fragment expression.
 * 
 * @author Emanuel Rabina
 */
@TupleConstructor(defaults = false)
class FragmentParameterVariableUpdater {

	final String dialectPrefix
	final ITemplateContext context

	/**
	 * Given a fragment expression, update the local variables of the element
	 * being processed.
	 * 
	 * @param fragmentExpression
	 * @param fragment
	 * @param structureHandler
	 */
	void updateLocalVariables(FragmentExpression fragmentExpression, IModel fragment,
		IElementModelStructureHandler structureHandler) {

		// When fragment parameters aren't named, derive the name from the fragment definition
		if (fragmentExpression.hasSyntheticParameters()) {
			def fragmentDefinition = fragment.first().getAttributeValue(dialectPrefix, FragmentProcessor.PROCESSOR_NAME)
			def parameterNames = new FragmentParameterNamesExtractor().extract(fragmentDefinition)
			fragmentExpression.parameters.eachWithIndex { parameter, index ->
				structureHandler.setLocalVariable(parameterNames[index], parameter.right.execute(context))
			}
		}
		// Otherwise, apply values as is
		else {
			fragmentExpression.parameters.each { parameter ->
				structureHandler.setLocalVariable(parameter.left.execute(context), parameter.right.execute(context))
			}
		}
	}
}
