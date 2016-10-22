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

import org.thymeleaf.standard.expression.Assignation

/**
 * Representation of a scoped variable declaration made through {@code th:with}
 * attributes.  This is really a wrapper around Thymeleaf's {@link Assignation}
 * class, but simplified to just the left and right hand components in string
 * form.
 * 
 * @author Emanuel Rabina
 */
class VariableDeclaration {

	final String name
	final String value

	/**
	 * Constructor, create an instance from a Thymeleaf assignation.
	 * 
	 * @param assignation
	 */
	VariableDeclaration(Assignation assignation) {

		def declaration = assignation.stringRepresentation
		def equalsIndex = declaration.indexOf('=')

		name = declaration.substring(0, equalsIndex)
		value = declaration.substring(equalsIndex + 1)
	}

	/**
	 * Reconstructs the variable for use with {@code th:with}.
	 * 
	 * @return {name}=${value}
	 */
	@Override
	String toString() {

		return "${name}=${value}"
	}
}
