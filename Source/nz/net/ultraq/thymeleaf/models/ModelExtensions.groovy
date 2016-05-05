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

import org.thymeleaf.engine.TemplateModel
import org.thymeleaf.model.IAttribute
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
			 * Returns the first event in the model, which is the element the model
			 * represents.
			 * 
			 * @return This model's main event/element.
			 */
			getRootEvent << {
				return delegate.get(0)
			}

			/**
			 * Return whether or not a model has content by checking if it has any
			 * underlying events.
			 * 
			 * @return {@code true} if the model contains events.
			 */
			hasContent << {
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

			/**
			 * Sets the first event in the model, which is the element the model
			 * represents.
			 * 
			 * @param event
			 */
			setRootEvent << { event ->
				return delegate.replace(0, event)
			}
		}

		TemplateModel.metaClass {

			/**
			 * Shortcut to the template name found on the template data object.  Only
			 * works if the template was resolved via a name, rather than a string
			 * (eg: anonymous template), in which case this can return the entire
			 * template!
			 * 
			 * @return Template name.
			 */
			getTemplate << {
				return delegate.templateData.template
			}
		}

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
				return attributeName == "${prefix}:${name}" ?:
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
