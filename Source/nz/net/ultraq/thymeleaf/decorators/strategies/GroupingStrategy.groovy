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
import org.thymeleaf.model.IProcessableElementTag
import org.thymeleaf.model.ITemplateEvent

/**
 * The {@code <head>} merging strategy which groups like elements together.
 * 
 * @author Emanuel Rabina
 * @since 1.2.6
 */
class GroupingStrategy implements SortingStrategy {

	/**
	 * Returns the index of the last set of elements that are of the same 'type'
	 * as the content node.  eg: groups scripts with scripts, stylesheets with
	 * stylesheets, and so on.
	 * 
	 * @param headModel
	 * @param event
	 * @return Position of the end of the matching element group.
	 */
	int findPositionForContent(IModel headModel, ITemplateEvent event) {

		// Discard text/whitespace nodes
		if (event.whitespace) {
			return -1
		}

		def type = HeadEventTypes.findMatchingType(event)
		return headModel.findLastIndexOf { decoratorNode ->
			return type == HeadEventTypes.findMatchingType(decoratorNode)
		} + 1
	}


	/**
	 * Enum for the types of elements in the {@code <head>} section that we might
	 * need to sort.
	 */
	private static enum HeadEventTypes {

		COMMENT({ node ->
			return node instanceof IComment
		}),
		META({ node ->
			return node instanceof IProcessableElementTag && node.elementCompleteName == 'meta'
		}),
		STYLESHEET({ node ->
			return node instanceof IProcessableElementTag && node.elementCompleteName == 'link' &&
					node.getAttributeValue('rel') == 'stylesheet'
		}),
		SCRIPT({ node ->
			return node instanceof IProcessableElementTag && node.elementCompleteName == 'script'
		}),
		OTHER_ELEMENT({ node ->
			return node instanceof IElementTag
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
		 * Figure out the enum for the given event type.
		 * 
		 * @param event The event to match.
		 * @return Matching enum to describe the event.
		 */
		private static HeadEventTypes findMatchingType(ITemplateEvent event) {

			return values().find { headEventType ->
				return headEventType.determinant(event)
			}
		}
	}
}
