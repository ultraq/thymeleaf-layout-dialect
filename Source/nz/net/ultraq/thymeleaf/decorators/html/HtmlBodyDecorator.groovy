/* 
 * Copyright 2013, Emanuel Rabina (http://www.ultraq.net.nz/)
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

package nz.net.ultraq.thymeleaf.decorators.html

import nz.net.ultraq.thymeleaf.decorators.xml.XmlElementDecorator

import org.thymeleaf.model.IModel
import org.thymeleaf.model.IModelFactory

/**
 * A decorator specific to processing an HTML {@code <body>} element.
 * 
 * @author Emanuel Rabina
 */
class HtmlBodyDecorator extends XmlElementDecorator {

	/**
	 * Constructor, sets up the element decorator context.
	 * 
	 * @param modelFactory
	 */
	HtmlBodyDecorator(IModelFactory modelFactory) {

		super(modelFactory)
	}

	/**
	 * Decorate the {@code <body>} part.
	 * 
	 * @param targetBodyModel
	 * @param sourceBodyModel
	 */
	@Override
	void decorate(IModel targetBodyModel, IModel sourceBodyModel) {

		// Try to ensure there is a body as a result of decoration, applying the
		// source body, or just using what is in the target
		if (sourceBodyModel) {
			if (targetBodyModel) {
				super.decorate(targetBodyModel, sourceBodyModel)
			}
			else {
				targetBodyModel.replaceModel(sourceBodyModel)
			}
		}
	}
}
