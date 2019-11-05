/* 
 * Copyright 2015, Emanuel Rabina (http://www.ultraq.net.nz/)
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

package nz.net.ultraq.thymeleaf.decorators.strategies

import nz.net.ultraq.thymeleaf.decorators.SortingStrategy

import org.thymeleaf.model.IModel

/**
 * The standard {@code <head>} merging strategy, which simply appends the
 * content elements to the layout ones.
 * <p>
 * The default behaviour of the layout dialect has historically been to place
 * the {@code <title>} element at the beginning of the {@code <head>} element
 * during the decoration process; an arbitrary design decision which made
 * development of this library easier.  However, this runs against the
 * expectations of developers who wished to control the order of elements, most
 * notably the position of a {@code <meta charset...>} element.
 * <p>
 * This sorting strategy has been updated in 2.4.0 to retain this behaviour as
 * backwards compatibility with the 2.x versions of the layout dialect, but is
 * now deprecated and expected to be replaced by the
 * {@link AppendingRespectLayoutTitleStrategy} sorter from version 3.x onwards.
 * 
 * @author Emanuel Rabina
 */
@Deprecated
class AppendingStrategy implements SortingStrategy {

	/**
	 * Returns the position at the end of the {@code <head>} section.
	 * 
	 * @param headModel
	 * @param event
	 * @return The end of the head model.
	 */
	int findPositionForModel(IModel headModel, IModel childModel) {

		// Discard text/whitespace nodes
		if (childModel.whitespace) {
			return -1
		}

		def positions = headModel.size()

		// For backwards compatibility, match the location of any element at the
		// beginning of the <head> element.
		if (childModel.isElementOf('title')) {
			def firstElementIndex = headModel.findIndexOf(1) { event -> event.openingElement }
			if (firstElementIndex != -1) {
				return firstElementIndex
			}
			return positions > 2 ? 2 : 1
		}

		return positions - (positions > 2 ? 2 : 1)
	}
}
