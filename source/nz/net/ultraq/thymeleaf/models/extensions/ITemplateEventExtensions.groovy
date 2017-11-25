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

import org.thymeleaf.model.ITemplateEvent
import org.thymeleaf.model.IText

/**
 * Meta-programming extensions to the {@link ITemplateEvent} class.
 * 
 * @author Emanuel Rabina
 */
class ITemplateEventExtensions {

	/**
	 * Apply extensions to the {@code ITemplateEvent} class.
	 */
	static void apply() {

		ITemplateEvent.metaClass {

			/**
			 * Returns whether or not this event represents collapsible whitespace.
			 * 
			 * @return {@code true} if this is a collapsible text node.
			 */
			isWhitespace << {
				return delegate instanceof IText && delegate.whitespace
			}
		}
	}
}
