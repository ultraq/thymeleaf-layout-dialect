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

package nz.net.ultraq.thymeleaf.context

import org.thymeleaf.context.IContext

/**
 * Map of data and values that are passed around the layout dialect, exposed so
 * that others can make use of the properties.
 * 
 * @author Emanuel Rabina
 */
class LayoutContext extends HashMap<String,Object> {

	static final String CONTEXT_KEY = 'layout'

	/**
	 * Retrieve the layout dialect context on the Thymeleaf context.
	 * 
	 * @param context
	 * @return The existing layout dialect context, or `null` if none exists.
	 */
	static LayoutContext forContext(IContext context) {

		def dialectContext = context[CONTEXT_KEY]

		// Error if something has gone and taken this value.  Hopefully there aren't
		// any collisions, but this name isn't exactly rare, so it *just* might
		// happen.
		if (dialectContext && !(dialectContext instanceof LayoutContext)) {
			throw new IllegalStateException(
				'Name collision on the Thymeleaf processing context.  ' +
				'An object with the key "layout" already exists, but is needs to be free for the Layout Dialect to work.'
			)
		}

		return dialectContext
	}
}
