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

import java.util.regex.Pattern

/**
 * Representation of a scoped variable declaration made through
 * <tt>th:with</tt> attributes.
 * 
 * @author Emanuel Rabina
 */
class VariableDeclaration {

	static final Pattern DECLARATION_PATTERN = ~/(.*?)=(.*)/

	final String name
	final String value

	/**
	 * Constructor, create an instance from the declaration string.
	 * 
	 * @param declaration
	 */
	VariableDeclaration(String declaration) {

		def matcher = DECLARATION_PATTERN.matcher(declaration)
		if (matcher.matches()) {
			name  = matcher.group(1)
			value = matcher.group(2)
		}
		else {
			throw new IllegalArgumentException("Unable to derive attribte declaration from string ${declaration}")
		}
	}

	/**
	 * Reconstructs the variable for use with <tt>th:with</tt>.
	 * 
	 * @return {name}=${value}
	 */
	@Override
	String toString() {

		return "${name}=${value}"
	}
}
