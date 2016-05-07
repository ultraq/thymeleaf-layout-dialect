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
import org.thymeleaf.model.IModelFactory

/**
 * Merges an element and all its children into an existing element.
 * 
 * @author Emanuel Rabina
 */
class ElementMerger implements ModelMerger {

	private final IModelFactory modelFactory

	/**
	 * Constructor, sets up the attribute merger tools.
	 * 
	 * @param modelFactory
	 */
	ElementMerger(IModelFactory modelFactory) {

		this.modelFactory = modelFactory
	}

	/**
	 * Flag for indicating that the merge is over a root element, in which some
	 * special rules apply.
	 */
	// TODO: Do I need this?  Let's try get by without it...
//	final boolean rootElementMerge

	/**
	 * Replace the content of the target element, with the content of the source
	 * element.
	 * 
	 * @param targetModel
	 * @param sourceModel
	 */
	@Override
	void merge(IModel targetModel, IModel sourceModel) {

		// Because we're basically replacing targetModel with sourceModel, we'll
		// lose the attributes in the target.  So, create a copy of those attributes
		// for that merge after.
		def targetInitialRootElement = modelFactory.createModel(targetModel.get(0))

		// TODO: Shouldn't all this be done with the structureHandler?  I should
		//       make another code branch that does that, and then I can compare.

		// Replace the target model with the source model
		targetModel.replaceModel(sourceModel)

		new AttributeMerger(modelFactory).merge(targetModel, targetInitialRootElement)

		// Create a new merged element to mess with
/*		def mergedElement = sourceElement.cloneNode(null, false)
		if (!rootElementMerge) {
			mergedElement.clearAttributes()
			targetElement.attributeMap.values().each { attribute ->
				mergedElement.setAttribute(attribute.normalizedName, attribute.value)
			}
			super.merge(mergedElement, sourceElement)
		}
		targetElement.clearChildren()
		targetElement.addChild(mergedElement)
		targetElement.parent.extractChild(targetElement)
*/	}
}
