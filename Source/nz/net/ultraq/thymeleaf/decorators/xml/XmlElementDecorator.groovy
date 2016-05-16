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

package nz.net.ultraq.thymeleaf.decorators.xml

import nz.net.ultraq.thymeleaf.decorators.Decorator
import nz.net.ultraq.thymeleaf.models.AttributeMerger

import org.thymeleaf.model.IModel
import org.thymeleaf.model.IModelFactory

/**
 * A decorator made to work over a Thymeleaf event/element.
 * 
 * TODO: I'm thinking this file is very redundant, given it's just an attribute
 *       merger.  See if I can delete this file and we use that instead.
 * 
 * @author Emanuel Rabina
 */
class XmlElementDecorator implements Decorator {

	protected final IModelFactory modelFactory

	/**
	 * Constructor, sets up the element decorator context.
	 * 
	 * @param modelFactory
	 */
	XmlElementDecorator(IModelFactory modelFactory) {

		this.modelFactory = modelFactory
	}

	/**
	 * Decorates the target element with the source element.  This step only
	 * merges the element attributes.  The body decoration is handled later on by
	 * fragment processors.
	 * 
	 * @param targetElementModel
	 * @param targetElementTemplate
	 * @param sourceElementModel
	 * @param sourceElementTemplate
	 */
	@Override
	void decorate(IModel targetElementModel, String targetElementTemplate,
		IModel sourceElementModel, String sourceElementTemplate) {

		new AttributeMerger(modelFactory).merge(targetElementModel, sourceElementModel)
	}
}
