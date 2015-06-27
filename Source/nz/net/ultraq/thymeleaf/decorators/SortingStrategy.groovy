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

package nz.net.ultraq.thymeleaf.decorators

import org.thymeleaf.dom.Node

/**
 * Interface for controlling the sort order in which elements are placed into
 * decorator template from the content one.
 * 
 * @author Emanuel Rabina
 */
interface SortingStrategy {

	/**
	 * Returns the position in amongst a list of the decorator's  nodes to
	 * insert a content child node.
	 * 
	 * @param decoratorNodes
	 * @param contentNode
	 * @return Index in the list of decorator nodes to insert the content node.
	 */
	int findPositionForContent(List<Node> decoratorNodes, Node contentNode)
}
