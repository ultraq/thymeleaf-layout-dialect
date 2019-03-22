/* 
 * Copyright 2019, Emanuel Rabina (http://www.ultraq.net.nz/)
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
 * A special version of the {@link GroupingStrategy} sorter that respects the
 * position of the {@code <title>} element within the layout page.
 * <p>
 * The default behaviour of the layout dialect has historically been to place
 * the {@code <title>} element at the beginning of the {@code <head>} element
 * during the decoration process; an arbitrary design decision which made
 * development of this library easier.  However, this runs against the
 * expectations of developers who wished to control the order of elements, most
 * notably the position of a {@code <meta charset...>} element.  This sorting
 * strategy instead keep {@code <title>}s wherever they exist within the
 * target/layout template being decorated, and then appending everything else as
 * normal.
 * <p>
 * This will become the default behaviour of the layout dialect from version 3.x
 * onwards, but was introduced in 2.4.0 to be a non-breaking change.
 * 
 * @author Emanuel Rabina
 * @since 2.4.0
 */
class GroupingRespectLayoutTitleStrategy implements SortingStrategy {

	/**
	 * For {@code <title>} elements, returns the position of the matching
	 * {@code <title>} in the {@code headModel} argument, otherwise returns the
	 * index of the last set of elements that are of the same 'type' as the
	 * content node.  eg: groups scripts with scripts, stylesheets with
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

		// Locate any matching <title> element
		if (childModel.isElementOf('title')) {
			def existingTitleIndex = headModel.findIndexOf { event -> event.isElementOf('title') }
			if (existingTitleIndex != -1) {
				return existingTitleIndex
			}
		}

		def type = HeadEventTypes.findMatchingType(childModel)
		def matchingModel = headModel.childModelIterator().reverse().find { headSubModel ->
			return type == HeadEventTypes.findMatchingType(headSubModel)
		}
		return matchingModel ? headModel.indexOf(matchingModel) + matchingModel.size() : 1
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
