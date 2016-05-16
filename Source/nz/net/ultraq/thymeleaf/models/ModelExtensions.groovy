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
import org.thymeleaf.model.IModelFactory
import org.thymeleaf.model.ITemplateEvent
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
			 * Iterate through each event in the model.  This is similar to what the
			 * {@code accept} method does.
			 * 
			 * @param closure
			 */
			each << { Closure closure ->
				for (def i = 0; i < delegate.size(); i++) {
					closure(delegate.get(i))
				}
			}

			/**
			 * Returns the first event in the model that meets the criteria of the
			 * given closure.
			 * 
			 * @param closure
			 * @return The first event to match the closure criteria, or {@code null}
			 *         if nothing matched.
			 */
			find << { Closure closure ->
				for (def i = 0; i < delegate.size(); i++) {
					def event = delegate.get(i)
					def result = closure(event)
					if (result) {
						return event;
					}
				}
				return null
			}

			/**
			 * Returns the index of the first event in the model that meets the
			 * criteria of the given closure.
			 * 
			 * @param closure
			 * @return The first event index to match the closure criteria, or
			 *         {@code null} if nothing matched.
			 */
			findIndex << { Closure closure ->
				for (def i = 0; i < delegate.size(); i++) {
					def event = delegate.get(i)
					def result = closure(event)
					if (result) {
						return i;
					}
				}
				return null
			}

			/**
			 * Returns the index of the last event in the model that meets the
			 * criteria of the given closure.
			 * 
			 * @param closure
			 * @return The index of the last event to match the closure criteria, or
			 *         -1 if no match was found.
			 */
			findLastIndexOf << { Closure closure ->
				for (def i = delegate.size() - 1; i >= 0; i--) {
					def event = delegate.get(i)
					def result = closure(event)
					if (result) {
						return i
					}
				}
				return -1
			}

			/**
			 * Returns the first event on the model.
			 * 
			 * @return The model's first event, or {@code null} if the model has no
			 *         events.
			 */
			first << {
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
			 * Inserts an event, creating a whitespace event before it so that it
			 * appears in line with all the existing events.
			 * 
			 * @param index
			 * @param event
			 * @param modelFactory
			 */
			insertWithWhitespace << { int index, ITemplateEvent event, IModelFactory modelFactory ->
				def whitespace = delegate.get(index)  // Assumes that whitespace exists at the current index
				if (whitespace.whitespace) {
					delegate.insert(index, event)
					delegate.insert(index, modelFactory.createText(whitespace.text))
				}
				else {
					delegate.insert(event)
				}
			}

			/**
			 * Returns the last event on the model.
			 * 
			 * @return The model's lats event, or {@code null} if the model has no
			 *         events.
			 */
			last << {
				return delegate.get(delegate.size() - 1)
			}

			/**
			 * Removes the first event on the model.
			 */
			removeFirst << {
				delegate.remove(0)
			}

			/**
			 * Removes the last event on the model.
			 */
			removeLast << {
				delegate.remove(delegate.size() - 1)
			}

			/**
			 * Replaces the enture model with a new one.
			 * 
			 * @param model
			 */
			replaceModel << { IModel model ->
				delegate.reset()
				delegate.addModel(model)
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
