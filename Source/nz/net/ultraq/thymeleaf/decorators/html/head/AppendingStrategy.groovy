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

import org.thymeleaf.dom.Element
import org.thymeleaf.dom.Node

/**
 * The standard &lt;head&gt; merging strategy, which is to simply append the
 * content to the decorator.
 * 
 * @author Emanuel Rabina
 */
class AppendingStrategy implements SortingStrategy {

	/**
	 * Returns a value to append the content node to the end of the decorator
	 * nodes.
	 * 
	 * @param decoratorHeadNodes
	 * @param contentNodes
	 * @return The size of the decorator nodes list.
	 */
	int findPositionForContent(List<Node> decoratorHeadNodes, Node contentNode) {

		return decoratorHeadNodes.findLastIndexOf { decoratorNode ->
			return decoratorNode instanceof Element
		} + 1
	}
}
