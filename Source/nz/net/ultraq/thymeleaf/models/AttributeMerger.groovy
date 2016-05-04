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

package nz.net.ultraq.thymeleaf.models

import org.thymeleaf.model.IModel
import org.thymeleaf.standard.StandardDialect

/**
 * Merges attributes from one element into another.
 * 
 * @author Emanuel Rabina
 */
class AttributeMerger implements ModelMerger {

	/**
	 * Merge the attributes of the source element with those of the target
	 * element.  This is basically a copy of all attributes in the source model
	 * with those in the target model, overwriting any attributes that have the
	 * same name, except for the case of {@code th:with} where variable
	 * declarations are preserved, only overwriting same-named declarations.
	 * 
	 * @param sourceModel
	 * @param targetModel
	 */
	@Override
	void merge(IModel targetModel, IModel sourceModel) {

		if (!sourceModel || !targetModel) {
			return
		}

		// Exclude the copying of fragment attributes
		def sourceAttributes = sourceModel.attributeMap.values().findAll { sourceAttribute ->
			return !sourceAttribute.equalsName(DIALECT_PREFIX_LAYOUT, PROCESSOR_NAME_FRAGMENT)
		}

		sourceAttributes.each { sourceAttribute ->

			// Merge th:with attributes to retain local variable declarations
			if (sourceAttribute.equalsName(StandardDialect.PREFIX, StandardWithAttrProcessor.ATTR_NAME)) {
				def mergedWithValue = sourceAttribute.value
				def targetWithValue = targetModel.getAttributeValue(StandardDialect.PREFIX, StandardWithAttrProcessor.ATTR_NAME)
				if (targetWithValue) {
					mergedWithValue += ",${targetWithValue}"
				}
				targetModel.setAttribute("${StandardDialect.PREFIX}:${StandardWithAttrProcessor.ATTR_NAME}", mergedWithValue)
			}

			// Copy every other attribute straight
			else {
				targetModel.setAttribute(sourceAttribute.originalName, sourceAttribute.value)
			}
		}
	}
}
