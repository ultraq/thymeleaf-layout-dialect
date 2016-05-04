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

package nz.net.ultraq.thymeleaf.fragments.mergers

/**
 * Merges variable declarations in a <tt>th:with</tt> attribute processor,
 * taking the declarations in the source and overriding same-named declarations
 * in the target.
 * 
 * @author Emanuel Rabina
 */
class VariableDeclarationMerger {

	/**
	 * Create variable declaration objects out of the declaration string.
	 * 
	 * @param declarationString
	 * @return A list of variable declaration objects that make up the declaration
	 *         string.
	 */
	private static ArrayList<VariableDeclaration> deriveDeclarations(declarationString) {

		def attributeTokens = declarationString.split(',')
		return attributeTokens.collect { attributeToken ->
			return new VariableDeclaration(attributeToken)
		}
	}

	/**
	 * Merge <tt>th:with</tt> attributes so that names from the source value
	 * overwrite the same names in the target value.
	 */
	String merge(String target, String source) {

		if (!target) {
			return source
		}
		if (!source) {
			return target
		}

		def targetDeclarations = deriveDeclarations(target)
		def sourceDeclarations = deriveDeclarations(source)

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
