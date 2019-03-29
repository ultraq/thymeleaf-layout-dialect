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

import org.thymeleaf.model.IComment
import org.thymeleaf.model.IElementTag
import org.thymeleaf.model.IModel
import org.thymeleaf.model.IOpenElementTag
import org.thymeleaf.model.IProcessableElementTag

/**
 * The {@code <head>} merging strategy which groups like elements together.
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
 * {@link GroupingRespectLayoutTitleStrategy} sorter from version 3.x onwards.
 * 
 * @author Emanuel Rabina
 * @since 1.2.6
 */
@Deprecated
class GroupingStrategy implements SortingStrategy {

	/**
	 * Returns the index of the last set of elements that are of the same 'type'
	 * as the content node.  eg: groups scripts with scripts, stylesheets with
	 * stylesheets, and so on.
	 * 
	 * @param headModel
	 * @param childModel
	 * @return Position of the end of the matching element group.
	 */
	int findPositionForModel(IModel headModel, IModel childModel) {

		// Discard text/whitespace nodes
		if (childModel.whitespace) {
			return -1
		}

		// For backwards compatibility, match the location of any element at the
		// beginning of the <head> element.
		if (childModel.isElementOf('title')) {
			def firstElementIndex = headModel.findIndexOf(1) { event -> event.openingElement }
			if (firstElementIndex != -1) {
				return firstElementIndex
			}
			return positions > 2 ? 2 : 1
		}

		def type = HeadEventTypes.findMatchingType(childModel)
		def matchingModel = headModel.childModelIterator().reverse().find { headSubModel ->
			return type == HeadEventTypes.findMatchingType(headSubModel)
		}
		return matchingModel ? headModel.findIndexOfModel(matchingModel) + matchingModel.size() : 1
	}


	/**
	 * Enum for the types of elements in the {@code <head>} section that we might
	 * need to sort.
	 */
	private static enum HeadEventTypes {

		COMMENT({ event ->
			return event instanceof IComment
		}),
		META({ event ->
			return event instanceof IProcessableElementTag && event.elementCompleteName == 'meta'
		}),
		SCRIPT({ event ->
			return event instanceof IOpenElementTag && event.elementCompleteName == 'script'
		}),
		STYLE({ event ->
			return event instanceof IOpenElementTag && event.elementCompleteName == 'style'
		}),
		STYLESHEET({ event ->
			return event instanceof IProcessableElementTag && event.elementCompleteName == 'link' &&
					event.getAttributeValue('rel') == 'stylesheet'
		}),
		OTHER({ event ->
			return event instanceof IElementTag
		})

		private final Closure determinant

		/**
		 * Constructor, set the test that matches this type of head node.
		 * 
		 * @param determinant
		 */
		private HeadEventTypes(Closure determinant) {

			this.determinant = determinant
		}

		/**
		 * Figure out the enum for the given model.
		 * 
		 * @param model
		 * @return Matching enum to describe the model.
		 */
		private static HeadEventTypes findMatchingType(IModel model) {

			return values().find { headEventType ->
				return headEventType.determinant(model.first())
			}
		}
	}
}
