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

import org.thymeleaf.engine.HTMLElementType
import org.thymeleaf.engine.TemplateModel
import org.thymeleaf.model.ICloseElementTag
import org.thymeleaf.model.IModel
import org.thymeleaf.model.IModelFactory
import org.thymeleaf.model.IOpenElementTag
import org.thymeleaf.model.ITemplateEvent
import org.thymeleaf.templatemode.TemplateMode

/**
 * Additional methods applied to the Thymeleaf {@link IModel} class via Groovy
 * meta-programming.
 * 
 * @author Emanuel Rabina
 */
class IModelExtensions {

	/**
	 * Applies several new methods to the {@code IModel} class.
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
			 * If this model represents an element, then this method returns an
			 * iterator over those child events.
			 * 
			 * @return An iterator over this model's child events, or an empty
			 *         iterator for all other model types.
			 */
			childEventIterator << {
				return delegate.element ? new ChildEventIterator(delegate) : null
			}

			/**
			 * If this model represents an element, then this method returns an
			 * iterator over any potential child items as models of their own.
			 * 
			 * @param modelFactory
			 * @return New model iterator.
			 */
			childModelIterator << {
				return delegate.element ? new ChildModelIterator(delegate) : null
			}

			/**
			 * Clears all the events from the model.
			 */
			clear << {
				delegate.reset()
			}

			/**
			 * If the model represents an element open to close tags, then this method
			 * removes all of the inner events.  Otherwise, it does nothing.
			 */
			clearChildren << {
				if (delegate.element) {
					while (delegate.size() > 2) {
						delegate.remove(1)
					}
				}
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
			 * Models returned via this method are also aware of their position in the
			 * event queue of the parent model, accessible via their {@code index}
			 * property.
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
						event.metaClass.index = i
						return event;
					}
				}
				return null
			}

			/**
			 * Returns the first instance of a model that meets the given closure
			 * criteria.
			 * 
			 * Models returned via this method are also aware of their start/end
			 * positions in the event queue of the parent model, accessible via their
			 * {@code startIndex}/{@code endIndex} properties.
			 * 
			 * @param closure
			 * @return A model over the event that matches the closure criteria, or
			 *         {@code null} if nothing matched.
			 */
			findModel << { Closure closure ->
				def event = delegate.find(closure)
				if (event) {
					def model = delegate.getModel(event.index)
					model.metaClass.startIndex = event.index
					model.metaClass.endIndex = event.index + model.size()
					return model
				}
				return null
			}

			/**
			 * Returns the first event in the model that meets the criteria of the
			 * given closure.
			 * 
			 * Models returned via this method are also aware of their position in the
			 * event queue of the parent model, accessible via their {@code index}
			 * property.
			 * 
			 * @param closure
			 * @return The first event to match the closure criteria, or {@code null}
			 *         if nothing matched.
			 */
			findWithIndex << { Closure closure ->
				for (def i = 0; i < delegate.size(); i++) {
					def event = delegate.get(i)
					def result = closure(event, i)
					if (result) {
						event.metaClass.index = i
						return event;
					}
				}
				return null
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
				def removeBefore = delegate instanceof TemplateModel ? pos - 1 : pos
				def removeAfter = subModel.size() - (removeBefore + modelSize)
				while (removeBefore-- > 0) {
					subModel.removeFirst()
				}
				while (removeAfter-- > 0) {
					subModel.removeLast()
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
				def whitespace = delegate.getModel(pos)  // Assumes that whitespace exists at the insertion point
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
			 * @param modelFactory
			 */
			insertWithWhitespace << { int pos, ITemplateEvent event, IModelFactory modelFactory ->

				// TODO: Because I can't check the parent for whitespace hints, I should
				//       make this smarter and find whitespace within the model to copy.
				def whitespace = delegate.getModel(pos)  // Assumes that whitespace exists at the insertion point
				if (whitespace.whitespace) {
					delegate.insert(pos, event)
					delegate.insertModel(pos, whitespace)
				}
				else {
					def newLine = modelFactory.createText('\n')
					if (pos == 0) {
						delegate.insert(pos, newLine)
						delegate.insert(pos, event)
					}
					else if (pos == delegate.size()) {
						delegate.insert(pos, newLine)
						delegate.insert(pos, event)
						delegate.insert(pos, newLine)
					}
				}
			}

			/**
			 * Returns whether or not this model represents an element with potential
			 * child elements.
			 * 
			 * @return {@code true} if the first event in this model is an opening tag
			 *         and the last event is the matching closing tag.
			 */
			isElement << {
				return delegate.first() instanceof IOpenElementTag && delegate.last() instanceof ICloseElementTag
			}

			/**
			 * Returns whether or not this model represents collapsible whitespace.
			 * 
			 * @return {@code true} if this is a collapsible text model.
			 */
			isWhitespace << {
				return delegate.size() == 1 && delegate.first().whitespace
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
			 * Replaces the model at the specified index with the given model.
			 * 
			 * @param pos
			 * @param model
			 */
			replaceModel << { int pos, IModel model ->
				delegate.removeModel(pos)
				delegate.insertModel(pos, model)
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
					if (event.templateMode == TemplateMode.HTML && event.elementDefinition.type == HTMLElementType.VOID) {
						// Do nothing.  This is to capture closing tags for HTML void
						// elements which shouldn't be specified according to the HTML spec.
						// https://html.spec.whatwg.org/multipage/syntax.html#void-elements
					}
					else if (level == 0) {
						break;
					}
					else {
						level--
					}
				}
			}
			return eventIndex - index
		}

		return 1
	}
}
