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

import org.thymeleaf.engine.TemplateModel

/**
 * A decorator made to work over a Thymeleaf event/element.
 * 
 * @author Emanuel Rabina
 */
class XmlElementDecorator implements Decorator {

	/**
	 * Decorates the target element with the source element.  This step only
	 * merges the element attributes.  The body decoration is handled later on by
	 * fragment processors.
	 * 
	 * @param targetElement
	 * @param sourceElement
	 */
	@Override
	void decorate(TemplateModel targetElement, TemplateModel sourceElement) {

		new AttributeMerger().merge(targetElement, sourceElement)
	}
}
