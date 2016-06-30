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

package nz.net.ultraq.thymeleaf.models.extensions

import org.thymeleaf.model.IAttribute

/**
 * Meta-programming extensions to the {@link IAttribute} class.
 * 
 * @author Emanuel Rabina
 */
class IAttributeExtensions {

	/**
	 * Apply extensions to the {@code IAttribute} class.
	 */
	static void apply() {

		IAttribute.metaClass {

			/**
			 * Returns whether or not an attribute is an attribute processor of
			 * the given name, checks both prefix:processor and
			 * data-prefix-processor variants.
			 * 
			 * @param prefix
			 * @param name
			 * @return {@code true} if this attribute is an attribute processor of the
			 *         matching name.
			 */
			equalsName << { String prefix, String name ->
				def attributeName = delegate.completeName
				return attributeName == "${prefix}:${name}" ||
				       attributeName == "data-${prefix}-${name}"
			}

			/**
			 * Shortcut to the attribute name class on the attribute definition.
			 * 
			 * @return Attribute name object.
			 */
			getAttributeName << {
				return delegate.definition.attributeName
			}
		}
	}
}
