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

import org.thymeleaf.model.IModel
import org.thymeleaf.model.IText

/**
 * Additional methods applied to the Thymeleaf model classes via Groovy
 * meta-programming.
 * 
 * @author Emanuel Rabina
 */
class ModelExtensions {

	/**
	 * Applies several new methods to the Thymeleaf model classes.
	 */
	static void apply() {

		IModel.metaClass {

			/**
			 * Return whether or not a model has a body, but checking if it has any
			 * underlying events.
			 * 
			 * @return {@code true} if the model contains events.
			 */
			hasBody << {
				return delegate.size() > 0
			}

			/**
			 * Returns whether or not this event represents collapsible whitespace.
			 * 
			 * @return {@code true} if this is a collapsible text node.
			 */
			isWhitespaceNode << {
				def thisEvent = delegate.get(0)
				return thisEvent instanceof IText && thisEvent.whitespace
			}
		}

		IText.metaClass {

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
