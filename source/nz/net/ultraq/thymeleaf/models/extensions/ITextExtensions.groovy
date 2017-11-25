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

import org.thymeleaf.model.IText

/**
 * Meta-programming extensions to the {@link IText} class.
 * 
 * @author Emanuel Rabina
 */
class ITextExtensions {

	/**
	 * Apply extensions to the {@code IText} class.
	 */
	static void apply() {

		IText.metaClass {

			/**
			 * Compares this text with another.
			 * 
			 * @param other
			 * @return {@code true} if the text content matches.
			 */
			equals << { Object other ->
				return other instanceof IText && delegate.text == other.text
			}

			/**
			 * Returns whether or not this text event is collapsible whitespace.
			 * 
			 * @return {@code true} if, when trimmed, the text content is empty.
			 */
			isWhitespace << {
				return delegate.text.trim().empty
			}
		}
	}
}
