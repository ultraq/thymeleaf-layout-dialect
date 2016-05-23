/* 
 * Copyright 2016, Emanuel Rabina (http://www.ultraq.net.nz/)
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

package nz.net.ultraq.thymeleaf.expressions

import org.thymeleaf.context.ITemplateContext
import org.thymeleaf.standard.expression.StandardExpressions

/**
 * Simple utility that hides the Thymeleaf scaffolding required to process
 * (parse and execute) an expression.
 * 
 * @author Emanuel Rabina
 */
class ExpressionProcessor {

	private final ITemplateContext context

	/**
	 * Constructor, sets the execution context.
	 * 
	 * @param context
	 */
	ExpressionProcessor(ITemplateContext context) {

		this.context = context
	}

	/**
	 * Process an expression, returning either an expression or object that best
	 * matches the expression being processed.
	 * 
	 * @param expression
	 * @return The expression object for the expression type.
	 */
	Object process(String expression) {

		return StandardExpressions.getExpressionParser(context.configuration)
			.parseExpression(context, expression)
			.execute(context)
	}

	/**
	 * Process an expression and return the result as a string.  Useful for
	 * expressions that expect a simple result, such as a template or fragment
	 * name.
	 * 
	 * @param expression
	 * @return The expression as a string.
	 */
	String processAsString(String expression) {

		return process(expression).toString()
	}
}
