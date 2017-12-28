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

import org.thymeleaf.model.IModel
import org.thymeleaf.model.IOpenElementTag

/**
 * Searches for and returns layout dialect fragments within a given
 * scope/element.
 * 
 * @author Emanuel Rabina
 */
class FragmentFinder {

	private final String dialectPrefix

	/**
	 * Constructor, create a new fragment finder to search for fragments using the
	 * given prefix as the one configured for the layout dialect.
	 * 
	 * @param dialectPrefix
	 */
	FragmentFinder(String dialectPrefix) {

		this.dialectPrefix = dialectPrefix
	}

	/**
	 * Find and return models for layout dialect fragments within the scope of the
	 * given model, without delving into {@code layout:include} or
	 * {@code layout:replace} elements, mapped by the name of each fragment.
	 * 
	 * @param model Model whose events are to be searched.
	 * @return Map of fragment names and their elements.
	 */
	Map<String,IModel> findFragments(IModel model) {

		def fragmentsMap = [:]

		def eventIndex = 0
		while (eventIndex < model.size()) {
			def event = model.get(eventIndex)
			if (event instanceof IOpenElementTag) {
				def fragmentName = event.getAttributeValue(dialectPrefix, FragmentProcessor.PROCESSOR_NAME)
				def collect = false
				if (!fragmentName) {
					collect = true
					fragmentName = event.getAttributeValue(dialectPrefix, CollectFragmentProcessor.PROCESSOR_DEFINE) ?: 
						event.getAttributeValue(dialectPrefix, CollectFragmentProcessor.PROCESSOR_COLLECT)
				}
				if (fragmentName) {
					def fragment = model.getModel(eventIndex)
					fragmentsMap[fragmentName] = fragmentsMap[fragmentName] ?: [] as Queue
					fragmentsMap[fragmentName] << fragment
					if (!collect) {
						eventIndex += fragment.size()
						continue
					}
				}
			}
			eventIndex++
		}

		return fragmentsMap
	}
}
