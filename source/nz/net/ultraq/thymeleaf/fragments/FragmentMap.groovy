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

import org.thymeleaf.context.IContext
import org.thymeleaf.model.IModel
import org.thymeleaf.processor.element.IElementModelStructureHandler

/**
 * Holds the layout fragments encountered across layout and content templates
 * for later use.
 * 
 * @author Emanuel Rabina
 */
class FragmentMap extends HashMap<String,IModel> {

	private static final String FRAGMENT_COLLECTION_KEY = 'LayoutDialect::FragmentCollection'

	/**
	 * Retrieves either the fragment map for the current context, or returns a new
	 * fragment map.
	 * 
	 * @param context
	 * @return A new or existing fragment collection for the context.
	 */
	static FragmentMap get(IContext context) {

		return context[FRAGMENT_COLLECTION_KEY] ?: new FragmentMap()
	}

	/**
	 * Set the fragment collection to contain whatever it initially had, plus the
	 * given fragments, just for the scope of the current node.
	 * 
	 * @param context
	 * @param structureHandler
	 * @param fragments The new fragments to add to the map.
	 */
	static void setForNode(IContext context, IElementModelStructureHandler structureHandler, Map<String,List> fragments) {

		def res = fragments
		def append = get(context)
		append.each {
			k, v ->
			if (res[k]) {
				res[k] += v
			}
			else {
				res[k] = v
			}
		}
		structureHandler.setLocalVariable(FRAGMENT_COLLECTION_KEY, res)
	}
}
