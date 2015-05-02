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

package nz.net.ultraq.thymeleaf.decorators.html.head

import org.thymeleaf.dom.Comment
import org.thymeleaf.dom.Element
import org.thymeleaf.dom.Node
import org.thymeleaf.dom.Text

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
	 * @param decoratorHeadNodes
	 * @param contentNodes
	 * @return Position of the end of the matching element group, or the 
	 */
	int findPositionForContent(List<Node> decoratorHeadNodes, Node contentNode) {

		// Discard text/whitespace nodes
		if (contentNode instanceof Text) {
			return -1
		}

		def type = HeadNodeTypes.findMatchingType(contentNode)
		return decoratorHeadNodes.findLastIndexOf { decoratorNode ->
			return type == HeadNodeTypes.findMatchingType(decoratorNode)
		} + 1
	}


	/**
	 * Enum for the types of elements in the HEAD section that we might need to
	 * sort.
	 * 
	 * TODO: Expand this to include more element types as they are requested.
	 */
	private static enum HeadNodeTypes {

		COMMENT({ node ->
			return node instanceof Comment
		}),
		META({ node ->
			return node instanceof Element && node.normalizedName == 'meta'
		}),
		STYLESHEET({ node ->
			return node instanceof Element && node.normalizedName == 'link' &&
					node.getAttributeValue('rel') == 'stylesheet'
		}),
		SCRIPT({ node ->
			return node instanceof Element && node.normalizedName == 'script'
		}),
		OTHER_ELEMENT({ node ->
			return node instanceof Element
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
		private static HeadNodeTypes findMatchingType(Node element) {

			return values().find { headNodeType ->
				return headNodeType.determinant(element)
			}
		}
	}
}
