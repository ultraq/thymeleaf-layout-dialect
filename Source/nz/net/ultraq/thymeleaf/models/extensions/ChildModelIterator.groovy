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

import org.thymeleaf.model.IModel

/**
 * This class provides a way for working with a model's immediate children, by
 * converting events into sub-models of their own.
 * 
 * @author Emanuel Rabina
 */
class ChildModelIterator implements Iterator<IModel> {

	private final IModel parent

	private int currentIndex = 1  // Starts after the root element

	/**
	 * Constructor, sets the model to iterate over.
	 * 
	 * @param parent
	 */
	ChildModelIterator(IModel parent) {

		this.parent = parent
	}

	/**
	 * Returns whether or not there is another model to be retrieved.
	 * 
	 * @return {@code true} if there are more events to process as models.
	 */
	@Override
	boolean hasNext() {

		return currentIndex < (parent.size() - 1)
	}

	/**
	 * Returns the next immediate child model of this model.
	 * 
	 * @return The next model in the iteration.
	 */
	@Override
	IModel next() {

		def subModel = parent.getModel(currentIndex)
		currentIndex += subModel.size()
		return subModel
	}

	/**
	 * Not applicable for this iterator.
	 * 
	 * @throws UnsupportedOperationException
	 */
	@Override
	void remove() {

		throw new UnsupportedOperationException()
	}
}
