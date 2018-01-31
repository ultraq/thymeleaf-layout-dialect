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

import nz.net.ultraq.thymeleaf.LayoutDialect
import nz.net.ultraq.thymeleaf.fragments.CollectFragmentProcessor
import nz.net.ultraq.thymeleaf.fragments.FragmentProcessor

import org.thymeleaf.context.ITemplateContext
import org.thymeleaf.model.IModel
import org.thymeleaf.standard.StandardDialect
import org.thymeleaf.standard.processor.StandardWithTagProcessor

/**
 * Merges attributes from one element into another.
 * 
 * @author Emanuel Rabina
 */
class AttributeMerger implements ModelMerger {

	private final ITemplateContext context

	/**
	 * Constructor, sets up the attribute merger context.
	 * 
	 * @param context
	 */
	AttributeMerger(ITemplateContext context) {

		this.context = context
	}

	/**
	 * Merge the attributes of the source element with those of the target
	 * element.  This is basically a copy of all attributes in the source model
	 * with those in the target model, overwriting any attributes that have the
	 * same name, except for the case of {@code th:with} where variable
	 * declarations are preserved, only overwriting same-named declarations.
	 * 
	 * @param sourceModel
	 * @param targetModel
	 * @return New element with the merged attributes.
	 */
	@Override
	IModel merge(IModel targetModel, IModel sourceModel) {

		// If one of the parameters is missing return a copy of the other, or
		// nothing if both parameters are missing.
		if (!targetModel || !sourceModel) {
			return targetModel?.cloneModel() ?: sourceModel?.cloneModel()
		}

		def mergedModel = targetModel.cloneModel()
		def layoutDialectPrefix = context.getPrefixForDialect(LayoutDialect)
		def standardDialectPrefix = context.getPrefixForDialect(StandardDialect)

		// Merge attributes from the source model's root event to the target model's root event
		sourceModel.first().allAttributes

			// Don't include layout:fragment processors
			.findAll { sourceAttribute ->
				return !sourceAttribute.equalsName(layoutDialectPrefix, FragmentProcessor.PROCESSOR_NAME) &&
					!sourceAttribute.equalsName(layoutDialectPrefix, CollectFragmentProcessor.PROCESSOR_DEFINE)
			}

			.each { sourceAttribute ->
				def mergedEvent = mergedModel.first()
				def mergedAttributeValue

				// Merge th:with attributes
				if (sourceAttribute.equalsName(standardDialectPrefix, StandardWithTagProcessor.ATTR_NAME)) {
					mergedAttributeValue = new VariableDeclarationMerger(context).merge(sourceAttribute.value,
						mergedEvent.getAttributeValue(standardDialectPrefix, StandardWithTagProcessor.ATTR_NAME))
				}

				// Copy every other attribute straight
				else {
					mergedAttributeValue = sourceAttribute.value
				}

				mergedModel.replace(0, context.modelFactory.replaceAttribute(mergedEvent,
					sourceAttribute.attributeName, sourceAttribute.completeName,
					mergedAttributeValue))
			}

		return mergedModel
	}
}
