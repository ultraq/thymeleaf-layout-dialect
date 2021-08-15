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

package nz.net.ultraq.thymeleaf.layoutdialect.models.extensions

import org.thymeleaf.context.IContext
import org.thymeleaf.model.IProcessableElementTag
import org.thymeleaf.standard.StandardDialect

/**
 * Meta-programming extensions to the {@link IProcessableElementTag} class.
 * 
 * @author Emanuel Rabina
 */
class IProcessableElementTagExtensions {

	/**
	 * Compare processable elements for equality.
	 * 
	 * @param self
	 * @param other
	 * @return {@code true} if this tag has the same name and attributes as
	 *         the other element.
	 */
	@SuppressWarnings('EqualsOverloaded')
	static boolean equals(IProcessableElementTag self, Object other) {
		return other instanceof IProcessableElementTag &&
			self.elementDefinition == other.elementDefinition &&
			self.attributeMap == other.attributeMap
	}

	/**
	 * Compare elements, ignoring XML namespace declarations and Thymeleaf's
	 * {@code th:with} processor.
	 * 
	 * @param self
	 * @param other
	 * @param context
	 * @return {@code true} if the elements share the same name and all attributes,
	 *         with exceptions for of XML namespace declarations and Thymeleaf's
	 *         {@code th:with} attribute processor.
	 */
	static boolean equalsIgnoreXmlnsAndWith(IProcessableElementTag self, IProcessableElementTag other, IContext context) {

		if (self.elementDefinition == other.elementDefinition) {
			def difference = self.attributeMap - other.attributeMap
			def standardDialectPrefix = context.getPrefixForDialect(StandardDialect)
			return difference.size() == 0 || difference
				.collect { key, value ->
					return key.startsWith('xmlns:') ||
						(key == "${standardDialectPrefix}:with" || key == "data-${standardDialectPrefix}-with")
				}
				.inject(true) { result, item -> result && item }
		}
		return false
	}
}
