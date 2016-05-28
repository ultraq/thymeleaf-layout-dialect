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
import org.thymeleaf.model.ICloseElementTag
import org.thymeleaf.model.IModel
import org.thymeleaf.model.IOpenElementTag
import org.thymeleaf.model.IStandaloneElementTag
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
			 * Set that a model evaluates to 'false' if it has no events.
			 * 
			 * @return {@code true} if this model has events.
			 */
			asBoolean << {
				return delegate.size() > 0
			}

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
			 * Compare 2 models, returning {@code true} if all of the model's events
			 * are equal.
			 * 
			 * @param other
			 * @return {@code true} if this model is the same as the other one.
			 */
			equals << { Object other ->
				if (other instanceof IModel && delegate.size() == other.size()) {
					return everyWithIndex { event, index ->
						return event == other.get(index)
					}
				}
				return false
			}

			/**
			 * Compare 2 models, returning {@code true} if all of the model's events
			 * non-whitespace events are equal.
			 * 
			 * @param other
			 * @return {@code true} if this model is the same (barring whitespace) as
			 *         the other one.
			 */
			equalsIgnoreWhitespace << { IModel other ->

				def thisEventIndex = 0
				def otherEventIndex = 0

				while (thisEventIndex < delegate.size() || otherEventIndex < other.size()) {
					def thisEvent = delegate.get(thisEventIndex)
					def otherEvent = other.get(otherEventIndex)
					if (thisEvent.whitespace) {
						thisEventIndex++
						continue
					}
					else if (otherEvent.whitespace) {
						otherEventIndex++
						continue
					}
					if (thisEvent != otherEvent) {
						return false
					}
					thisEventIndex++
					otherEventIndex++
				}

				if (thisEventIndex != delegate.size() || otherEventIndex != other.size()) {
					return false
				}

				return true
			}

			/**
			 * Return {@code true} only if all the events in the model return
			 * {@code true} for the given closure.
			 * 
			 * @param closure
			 * @return {@code true} if every event satisfies the closure.
			 */
			everyWithIndex << { Closure closure ->
				for (def i = 0; i < delegate.size(); i++) {
					if (!closure(delegate.get(i), i)) {
						return false
					}
				}
				return true
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
			findIndexOf << { Closure closure ->
				for (def i = 0; i < delegate.size(); i++) {
					def event = delegate.get(i)
					def result = closure(event)
					if (result) {
						return i;
					}
				}
				return -1
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
			 * Returns the first instance of a model that meets the given closure
			 * criteria.
			 * 
			 * @param closure
			 * @return A model over the event that matches the closure criteria, or
			 *         {@code null} if nothing matched.
			 */
			findModel << { Closure closure ->
				def eventIndex = findIndexOf(closure)
				return eventIndex != -1 ? getModel(eventIndex) : null
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
			 * Returns the model at the given index.  If the event at the index is an
			 * opening element, then the returned model will consist of that element
			 * and all the way through to the matching closing element.
			 * 
			 * @param pos
			 * @return Model at the given position.
			 */
			getModel << { int pos ->
				def modelSize = calculateModelSize(delegate, pos)
				def subModel = delegate.cloneModel()
				while (pos + modelSize < subModel.size()) {
					subModel.removeLast()
				}
				while (modelSize < subModel.size()) {
					subModel.removeFirst()
				}
				return subModel
			}

			/**
			 * Inserts a model, creating a whitespace event before it so that it
			 * appears in line with all the existing events.
			 * 
			 * @param pos
			 * @param model
			 */
			insertModelWithWhitespace << { int pos, IModel model ->
				def whitespace = getModel(pos)  // Assumes that whitespace exists at the insertion point
				if (whitespace.whitespace) {
					delegate.insertModel(pos, model)
					delegate.insertModel(pos, whitespace)
				}
				else {
					delegate.insertModel(pos, model)
				}
			}

			/**
			 * Inserts an event, creating a whitespace event before it so that it
			 * appears in line with all the existing events.
			 * 
			 * @param pos
			 * @param event
			 */
			insertWithWhitespace << { int pos, ITemplateEvent event ->
				def whitespace = delegate.getModel(pos)  // Assumes that whitespace exists at the insertion point
				if (whitespace.whitespace) {
					delegate.insert(pos, event)
					delegate.insertModel(pos, whitespace)
				}
				else {
					delegate.insert(event)
				}
			}

			/**
			 * Returns whether or not this model represents collapsible whitespace.
			 * 
			 * @return {@code true} if this is a collapsible text model.
			 */
			isWhitespace << {
				return delegate.size() == 1 && first().whitespace
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
			 * Returns a new model iterator over this model.
			 * 
			 * @param modelFactory
			 * @return New model iterator.
			 */
			modelIterator << {
				return new ModelIterator(delegate)
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
			 * Removes a models-worth of events from the specified position.  What
			 * this means is that, if the event at the position is an opening element,
			 * then it, and everything up to and including its matching end element,
			 * is removed.
			 * 
			 * @param pos
			 */
			removeModel << { int pos ->
				def modelSize = calculateModelSize(delegate, pos)
				while (modelSize > 0) {
					delegate.remove(pos)
					modelSize--
				}
			}

			/**
			 * Removes a models-worth of events from the specified position, plus the
			 * preceeding whitespace event if any.
			 * 
			 * @param pos
			 */
			removeModelWithWhitespace << { int pos ->
				removeModel(pos)
				def priorEvent = delegate.get(pos - 1)
				if (priorEvent.whitespace) {
					delegate.remove(pos - 1)
				}
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

		IOpenElementTag.metaClass {

			/**
			 * Compares this open tag with another.
			 * 
			 * @param other
			 * @return {@code true} if this tag has the same name and attributes as
			 *         the other element.
			 */
			equals << { Object other ->
				return other instanceof IOpenElementTag &&
						delegate.elementCompleteName == other.elementCompleteName &&
						delegate.attributeMap == other.attributeMap;
			}
		}

		ICloseElementTag.metaClass {

			/**
			 * Compares this close tag with another.
			 * 
			 * @param other
			 * @return {@code true} if this tag has the same name as the other
			 *         element.
			 */
			equals << { Object other ->
				return other instanceof ICloseElementTag &&
						delegate.elementCompleteName == other.elementCompleteName
			}
		}

		IStandaloneElementTag.metaClass {

			/**
			 * Compares this standalone tag with another.
			 * 
			 * @param other
			 * @return {@code true} if this tag has the same name and attributes as
			 *         the other element.
			 */
			equals << { Object other ->
				return other instanceof IStandaloneElementTag &&
					delegate.elementCompleteName == other.elementCompleteName &&
					delegate.attributeMap == other.attributeMap;
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

	/**
	 * If an opening element exists at the given position, this method will
	 * return the 'size' of that element (number of events from here to its
	 * matching closing tag).  Otherwise, a size of 1 is returned.
	 * 
	 * @param model
	 * @param index
	 * @return Size of an element from the given position, or 1 if the event
	 *         at the position isn't an opening element.
	 */
	private static int calculateModelSize(IModel model, int index) {

		def eventIndex = index
		def event = model.get(eventIndex++)

		if (event instanceof IOpenElementTag) {
			def level = 0
			while (true) {
				event = model.get(eventIndex++)
				if (event instanceof IOpenElementTag) {
					level++
				}
				if (event instanceof ICloseElementTag) {
					if (level == 0) {
						break;
					}
					level--
				}
			}
			return eventIndex - index
		}

		return 1
	}
}
