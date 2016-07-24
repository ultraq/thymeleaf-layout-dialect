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

import org.thymeleaf.context.ITemplateContext
import org.thymeleaf.model.AttributeValueQuotes
import org.thymeleaf.model.IModel
import org.thymeleaf.model.IOpenElementTag
import org.thymeleaf.model.IStandaloneElementTag

/**
 * Merges an element and all its children into an existing element.
 * 
 * @author Emanuel Rabina
 */
class ElementMerger implements ModelMerger {

	private final ITemplateContext context

	/**
	 * Constructor, sets up the element merger context.
	 * 
	 * @param context
	 */
	ElementMerger(ITemplateContext context) {

		this.context = context
	}

	/**
	 * Replace the content of the target element, with the content of the source
	 * element.
	 * 
	 * @param targetModel
	 * @param sourceModel
	 * @return Model that is the result of the merge.
	 */
	@Override
	IModel merge(IModel targetModel, IModel sourceModel) {

		// If one of the parameters is missing return a copy of the other, or
		// nothing if both parameters are missing.
		if (!targetModel || !sourceModel) {
			return targetModel?.cloneModel() ?: sourceModel?.cloneModel()
		}

		def modelFactory = context.modelFactory

		// The result we want is the source model, but merged into the target root element attributes
		def sourceRootEvent = sourceModel.first()
		def sourceRootElement = modelFactory.createModel(sourceRootEvent)
		def targetRootEvent = targetModel.first()
		def targetRootElement = modelFactory.createModel(
			sourceRootEvent instanceof IOpenElementTag ?
				modelFactory.createOpenElementTag(sourceRootEvent.elementCompleteName,
					targetRootEvent.attributeMap, AttributeValueQuotes.DOUBLE, false) :
			sourceRootEvent instanceof IStandaloneElementTag ?
				modelFactory.createStandaloneElementTag(sourceRootEvent.elementCompleteName,
					targetRootEvent.attributeMap, AttributeValueQuotes.DOUBLE, false, sourceRootEvent.minimized) :
			null)
		def mergedRootElement = new AttributeMerger(context).merge(targetRootElement, sourceRootElement)
		def mergedModel = sourceModel.cloneModel()
		mergedModel.replace(0, mergedRootElement.first())
		return mergedModel
	}
}
