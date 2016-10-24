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

package nz.net.ultraq.thymeleaf.models

import org.thymeleaf.context.IExpressionContext

/**
 * Merges variable declarations in a {@code th:with} attribute processor, taking
 * the declarations in the target and combining them with the declarations in
 * the source, overriding any same-named declarations in the target.
 * 
 * @author Emanuel Rabina
 */
class VariableDeclarationMerger {

	private final IExpressionContext context

	/**
	 * Constructor, sets the processing context for the merger.
	 * 
	 * @oaram context
	 */
	VariableDeclarationMerger(IExpressionContext context) {

		this.context = context
	}

	/**
	 * Merge {@code th:with} attributes so that names from the source value
	 * overwrite the same names in the target value.
	 */
	String merge(String target, String source) {

		if (!target) {
			return source
		}
		if (!source) {
			return target
		}

		def declarationParser = new VariableDeclarationParser(context)
		def targetDeclarations = declarationParser.parse(target)
		def sourceDeclarations = declarationParser.parse(source)

		def newDeclarations = []
		targetDeclarations.each { targetDeclaration ->
			def override = sourceDeclarations.find { sourceDeclaration ->
				return sourceDeclaration.name == targetDeclaration.name
			}
			if (override) {
				sourceDeclarations.remove(override)
				newDeclarations << override
			}
			else {
				newDeclarations << targetDeclaration
			}
		}
		sourceDeclarations.each { targetAttributeDeclaration ->
			newDeclarations << targetAttributeDeclaration
		}

		return newDeclarations.join(',')
	}
}
