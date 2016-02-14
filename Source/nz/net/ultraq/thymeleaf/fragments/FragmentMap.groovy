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

package nz.net.ultraq.thymeleaf.fragments

import org.thymeleaf.Arguments
import org.thymeleaf.dom.Element
import org.thymeleaf.dom.Node

/**
 * Holds the layout fragments encountered across layout/decorator and content
 * templates for use later.
 * 
 * @author Emanuel Rabina
 */
class FragmentMap extends HashMap<String,Element> {

	private static final String FRAGMENT_COLLECTION_KEY = 'LayoutDialect::FragmentCollection'

	/**
	 * Retrieve the fragment collection specific to the given context.  If none
	 * exists, a new collection is created, applied to the context, and
	 * returned.
	 * 
	 * @param arguments
	 * @return A new or existing fragment collection for the context.
	 */
	static FragmentMap get(Arguments arguments) {

		return arguments.getLocalVariable(FRAGMENT_COLLECTION_KEY) ?: [:]
	}

	/**
	 * Updates the fragment collection just for the current node.
	 * 
	 * @param arguments
	 * @param node
	 * @param fragments The new fragments to add to the map.
	 */
	static void updateForNode(Arguments arguments, Node node, Map<String,Element> fragments) {

		node.setNodeLocalVariable(FRAGMENT_COLLECTION_KEY, get(arguments) << fragments)
		
	}
}
