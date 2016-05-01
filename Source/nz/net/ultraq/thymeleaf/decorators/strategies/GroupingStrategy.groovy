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
 * The &lt;head&gt; merging strategy introduced in version 1.2.6 of the Layout
 * dialect, which groups like elements together.
 * 
 * @author Emanuel Rabina
 */
class GroupingStrategy implements SortingStrategy {

	/**
	 * Returns the index of the last set of elements that are of the same 'type'
	 * as the content node.  eg: groups scripts with scripts, stylesheets with
	 * stylesheets, and so on.
	 * 
	 * @param decoratorNodes
	 * @param contentNodes
	 * @return Position of the end of the matching element group.
	 */
	int findPositionForContent(List<IModel> decoratorNodes, IModel contentNode) {

		// Discard text/whitespace nodes
		if (contentNode.whitespaceNode) {
			return -1
		}

		def type = HeadNodeTypes.findMatchingType(contentNode)
		return decoratorNodes.findLastIndexOf { decoratorNode ->
			return type == HeadNodeTypes.findMatchingType(decoratorNode)
		} + 1
	}


	/**
	 * Enum for the types of elements in the HEAD section that we might need to
	 * sort.
	 */
	private static enum HeadNodeTypes {

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
		private HeadNodeTypes(Closure determinant) {

			this.determinant = determinant
		}

		/**
		 * Figure out the enum for the given node type.
		 * 
		 * @param element The node to match.
		 * @return Matching <tt>HeadNodeTypes</tt> enum to descript the node.
		 */
		private static HeadNodeTypes findMatchingType(ITemplateEvent element) {

			return values().find { headNodeType ->
				return headNodeType.determinant(element)
			}
		}
	}
}
