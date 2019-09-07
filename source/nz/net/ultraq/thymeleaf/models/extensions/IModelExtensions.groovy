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

import org.thymeleaf.engine.TemplateModel
import org.thymeleaf.model.ICloseElementTag
import org.thymeleaf.model.IModel
import org.thymeleaf.model.IModelFactory
import org.thymeleaf.model.IOpenElementTag
import org.thymeleaf.model.ITemplateEvent

/**
 * Meta-programming extensions to the {@link IModel} class.
 * 
 * @author Emanuel Rabina
 */
@SuppressWarnings(['EmptyIfStatement', 'MethodSize', 'NestedBlockDepth', 'UnnecessaryCallForLastElement'])
class IModelExtensions {

	/**
	 * Set that a model evaluates to 'false' if it has no events.
	 * 
	 * @param self
	 * @return {@code true} if this model has events.
	 */
	static asBoolean(IModel self) {
		return self.size() > 0
	}

	/**
	 * If this model represents an element, then this method returns an
	 * iterator over any potential child items as models of their own.
	 * 
	 * @param self
	 * @param modelFactory
	 * @return New model iterator.
	 */
	static childModelIterator(IModel self) {
		return self.element ? new ChildModelIterator(self) : null
	}

	/**
	 * Iterate through each event in the model.
	 * 
	 * @param self
	 * @param closure
	 */
	static each(IModel self, Closure closure) {
		self.iterator().each(closure)
	}

	/**
	 * Compare 2 models, returning {@code true} if all of the model's events
	 * are equal.
	 * 
	 * @param self
	 * @param other
	 * @return {@code true} if this model is the same as the other one.
	 */
	@SuppressWarnings('EqualsOverloaded')
	static equals(IModel self, Object other) {
		if (other instanceof IModel && self.size() == other.size()) {
			return self.everyWithIndex { event, index -> event == other.get(index) }
		}
		return false
	}

	/**
	 * Compare 2 models, returning {@code true} if all of the model's events
	 * non-whitespace events are equal.
	 * 
	 * @param self
	 * @param other
	 * @return {@code true} if this model is the same (barring whitespace) as
	 *         the other one.
	 */
	static equalsIgnoreWhitespace(IModel self, IModel other) {
		if (other instanceof IModel) {
			def nonWhitespaceEvents = { event -> !event.whitespace }
			return self.findAll(nonWhitespaceEvents) == other.findAll(nonWhitespaceEvents)
		}
		return false
	}

	/**
	 * Return {@code true} only if all the events in the model return
	 * {@code true} for the given closure.
	 * 
	 * @param self
	 * @param closure
	 * @return {@code true} if every event satisfies the closure.
	 */
	static everyWithIndex(IModel self, Closure closure) {
		for (def i = 0; i < self.size(); i++) {
			if (!closure(self.get(i), i)) {
				return false
			}
		}
		return true
	}

	/**
	 * Returns the first event in the model that meets the criteria of the
	 * given closure.
	 * 
	 * @param self
	 * @param closure
	 * @return The first event to match the closure criteria, or {@code null}
	 *         if nothing matched.
	 */
	static find(IModel self, Closure closure) {
		return self.iterator().find(closure)
	}

	/**
	 * Find all events in the model that match the given closure.
	 * 
	 * @param self
	 * @param closure
	 * @return A list of matched events.
	 */
	static findAll(IModel self, Closure closure) {
		return self.iterator().findAll(closure)
	}

	/**
	 * Returns the index of the first event in the model that meets the
	 * criteria of the given closure.
	 * 
	 * @param self
	 * @param closure
	 * @return The index of the first event to match the closure criteria, or
	 *         {@code -1} if nothing matched.
	 */
	static findIndexOf(IModel self, Closure closure) {
		return self.iterator().findIndexOf(closure)
	}

	/**
	 * Returns the index of the first event in the model that meets the
	 * criteria of the given closure, starting from a specified position.
	 * 
	 * @param self
	 * @param closure
	 * @return The index of the first event to match the closure criteria, or
	 *         {@code -1} if nothing matched.
	 */
	static findIndexOf(IModel self, int startIndex, Closure closure) {
		return self.iterator().findIndexOf(startIndex, closure)
	}

	/**
	 * A special variant of {@code findIndexOf} that uses models, as I seem to
	 * be using those a lot.
	 * 
	 * This doesn't use an equality check, but an object reference check, so
	 * if a submodel is ever located from a parent (eg: any of the {@code find}
	 * methods, you can use this method to find the location of that submodel
	 * within the event queue.
	 * 
	 * @param self
	 * @param model
	 * @return Index of an extracted submodel within this model.
	 */
	static findIndexOfModel(IModel self, IModel model) {
		def modelEvent = model.first()
		return self.findIndexOf { event -> event.is(modelEvent) }
	}

	/**
	 * Returns the first instance of a model that meets the given closure
	 * criteria.
	 * 
	 * @param self
	 * @param closure
	 * @return A model over the event that matches the closure criteria, or
	 *         {@code null} if nothing matched.
	 */
	static findModel(IModel self, Closure closure) {
		return self.getModel(self.findIndexOf(closure))
	}

	/**
	 * Returns the first event on the model.
	 * 
	 * @param self
	 * @return The model's first event.
	 */
	static first(IModel self) {
		return self.get(0)
	}

	/**
	 * Returns the model at the given index.  If the event at the index is an
	 * opening element, then the returned model will consist of that element
	 * and all the way through to the matching closing element.
	 * 
	 * @param self
	 * @param pos A valid index within the current model.
	 * @return Model at the given position, or `null` if the position is
	 *         outside of the event queue.
	 */
	static getModel(IModel self, int pos) {
		if (0 <= pos && pos < self.size()) {
			def clone = self.cloneModel()
			def removeBefore = self instanceof TemplateModel ? pos - 1 : pos
			def removeAfter = clone.size() - (removeBefore + self.sizeOfModelAt(pos))
			while (removeBefore-- > 0) {
				clone.removeFirst()
			}
			while (removeAfter-- > 0) {
				clone.removeLast()
			}
			return clone
		}
		return null
	}

	/**
	 * Inserts a model, creating a whitespace event before it so that it
	 * appears in line with all the existing events.
	 * 
	 * @param self
	 * @param pos          A valid index within the current model.
	 * @param model
	 * @param modelFactory
	 */
	static insertModelWithWhitespace(IModel self, int pos, IModel model, IModelFactory modelFactory) {

		if (0 <= pos && pos <= self.size()) {

			// Use existing whitespace found at or before the insertion point
			def whitespace = self.getModel(pos)
			if (whitespace?.whitespace) {
				self.insertModel(pos, model)
				self.insertModel(pos, whitespace)
				return
			}
			if (pos > 0) {
				whitespace = self.getModel(pos - 1)
				if (whitespace?.whitespace) {
					self.insertModel(pos, whitespace)
					self.insertModel(pos, model)
					return
				}
			}

			// Generate whitespace on either side of the model to insert
			whitespace = modelFactory.createModel(modelFactory.createText('\n\t'))
			self.insertModel(pos, whitespace)
			self.insertModel(pos, model)
			self.insertModel(pos, whitespace)
		}
	}

	/**
	 * Inserts an event, creating a whitespace event before it so that it
	 * appears in line with all the existing events.
	 * 
	 * @param self
	 * @param pos          A valid index within the current model.
	 * @param event
	 * @param modelFactory
	 */
	static insertWithWhitespace(IModel self, int pos, ITemplateEvent event, IModelFactory modelFactory) {

		if (0 <= pos && pos <= self.size()) {

			// TODO: Because I can't check the parent for whitespace hints, I
			//       should make this smarter and find whitespace within the model
			//       to copy.
			def whitespace = self.getModel(pos)
			if (whitespace?.whitespace) {
				self.insert(pos, event)
				self.insertModel(pos, whitespace)
			}
			else {
				def newLine = modelFactory.createText('\n')
				if (pos == 0) {
					self.insert(pos, newLine)
					self.insert(pos, event)
				}
				else if (pos == self.size()) {
					self.insert(pos, newLine)
					self.insert(pos, event)
					self.insert(pos, newLine)
				}
			}
		}
	}

	/**
	 * Returns whether or not this model represents an element with potential
	 * child elements.
	 * 
	 * @param self
	 * @return {@code true} if the first event in this model is an opening tag
	 *         and the last event is the matching closing tag.
	 */
	static isElement(IModel self) {
		return self.first().openingElement && self.last().closingElement
	}

	/**
	 * Returns whether or not this model represents an element of the given
	 * name.
	 * 
	 * @param self
	 * @param tagName
	 * @return {@code true} if the first event in this model is an opening tag,
	 *         the last event is the matching closing tag, and  whether the
	 *         element has the given tag name.
	 */
	static isElementOf(IModel self, tagName) {
		return self.element && self.first().elementCompleteName == tagName
	}

	/**
	 * Returns whether or not this model represents collapsible whitespace.
	 * 
	 * @param self
	 * @return {@code true} if this is a collapsible text model.
	 */
	static isWhitespace(IModel self) {
		return self.size() == 1 && self.first().whitespace
	}

	/**
	 * Used to make this class iterable as an event queue.
	 * 
	 * @param self
	 * @return A new iterator over the events of this model.
	 */
	static iterator(IModel self) {
		return new EventIterator(self)
	}

	/**
	 * Returns the last event on the model.
	 * 
	 * @param self
	 * @return The model's last event.
	 */
	static last(IModel self) {
		return self.get(self.size() - 1)
	}

	/**
	 * If the model represents an element open to close tags, then this method
	 * removes all of the inner events.
	 * 
	 * @param self
	 */
	static removeChildren(IModel self) {
		if (self.element) {
			while (self.size() > 2) {
				self.remove(1)
			}
		}
	}

	/**
	 * Removes the first event on the model.
	 * 
	 * @param self
	 */
	static removeFirst(IModel self) {
		self.remove(0)
	}

	/**
	 * Removes the last event on the model.
	 * 
	 * @param self
	 */
	static removeLast(IModel self) {
		self.remove(self.size() - 1)
	}

	/**
	 * Removes a models-worth of events from the specified position.  What
	 * this means is that, if the event at the position is an opening element,
	 * then it, and everything up to and including its matching end element,
	 * is removed.
	 * 
	 * @param self
	 * @param pos A valid index within the current model.
	 */
	static removeModel(IModel self, int pos) {
		if (0 <= pos && pos < self.size()) {
			def modelSize = self.sizeOfModelAt(pos)
			while (modelSize > 0) {
				self.remove(pos)
				modelSize--
			}
		}
	}

	/**
	 * Replaces the model at the specified index with the given model.
	 * 
	 * @param self
	 * @param pos   A valid index within the current model.
	 * @param model
	 */
	static replaceModel(IModel self, int pos, IModel model) {
		if (0 <= pos && pos < self.size()) {
			self.removeModel(pos)
			self.insertModel(pos, model)
		}
	}

	/**
	 * If an opening element exists at the given position, this method will
	 * return the 'size' of that element (number of events from here to its
	 * matching closing tag).
	 * 
	 * @param self
	 * @param model
	 * @param index
	 * @return Size of an element from the given position, or 1 if the event
	 *         at the position isn't an opening element.
	 */
	static sizeOfModelAt(IModel self, int index) {

		def eventIndex = index
		def event = self.get(eventIndex++)

		if (event instanceof IOpenElementTag) {
			def level = 0
			while (true) {
				event = self.get(eventIndex++)
				if (event instanceof IOpenElementTag) {
					level++
				}
				else if (event instanceof ICloseElementTag) {
					if (event.unmatched) {
						// Do nothing.  Unmatched closing tags do not correspond to any
						// opening element, and so should not affect the model level.
					}
					else if (level == 0) {
						break
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

	/**
	 * Removes whitespace events from the head and tail of the model's
	 * underlying event queue.
	 * 
	 * @param self
	 */
	static trim(IModel self) {
		while (self.first().whitespace) {
			self.removeFirst()
		}
		while (self.last().whitespace) {
			self.removeLast()
		}
	}
}
