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

package nz.net.ultraq.thymeleaf.utilities

import org.thymeleaf.context.ITemplateContext
import org.thymeleaf.standard.expression.StandardExpressions

/**
 * Simple utility that hides the Thymeleaf scaffolding required to process
 * (parse and execute) an expression.  Used for the very basic expressions used
 * with the layout dialect that always result in a template or fragment name.
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
	 * Process an expression given to the layout dialect, returning the string
	 * that names a template or a fragment.
	 * 
	 * @param expression
	 * @return Template or fragment name string.
	 */
	String process(String expression) {

		return StandardExpressions.getExpressionParser(context.configuration)
			.parseExpression(context, expression)
			.execute(context)
			.toString()
	}
}
