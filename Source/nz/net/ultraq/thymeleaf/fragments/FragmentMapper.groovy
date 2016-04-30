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

import nz.net.ultraq.thymeleaf.fragments.FragmentProcessor
import nz.net.ultraq.thymeleaf.includes.IncludeProcessor
import nz.net.ultraq.thymeleaf.includes.ReplaceProcessor
import static nz.net.ultraq.thymeleaf.LayoutDialect.DIALECT_PREFIX

import org.thymeleaf.context.IContext
import org.thymeleaf.context.ITemplateContext
import org.thymeleaf.model.IElementTag
import org.thymeleaf.model.IModel
import org.thymeleaf.model.IModelVisitor
import org.thymeleaf.model.IOpenElementTag
import org.thymeleaf.standard.expression.Fragment

import groovy.transform.TupleConstructor

/**
 * Searches for and returns layout dialect fragments within a given element.
 * 
 * @author Emanuel Rabina
 */
@TupleConstructor
class FragmentMapper {

	final ITemplateContext context

	/**
	 * Find and return fragments within the given element, without delving into
	 * <tt>layout:include</tt> or <tt>layout:replace</tt> elements, mapped by the
	 * name of each fragment.
	 * 
	 * @param model Element whose children are to be searched.
	 * @return Map of fragment names and their elements.
	 */
	Map<String,IModel> map(IModel model) {

		def fragments = [:]
		def isLayoutElement = { elementTag ->
			return elementTag.hasAttribute(DIALECT_PREFIX, IncludeProcessor.PROCESSOR_NAME) ||
			       elementTag.hasAttribute(DIALECT_PREFIX, ReplaceProcessor.PROCESSOR_NAME)
		}

		// NOTE: Using element definitions to match open and close tags, probably
		//       not going to work...
		def insideLayoutElementDefinition = null

		model.accept({ event ->
			if (event instanceof IOpenElementTag) {

				if (!insideLayoutElementDefinition) {
					def fragmentAttributeValue = event.getAttribute(DIALECT_PREFIX, FragmentProcessor.PROCESSOR_NAME)
					if (fragmentAttributeValue) {
						fragments << [(fragmentAttributeValue): context.modelFactory.createModel(event)]
					}

					if (isLayoutElement(event)) {
						insideLayoutElementDefinition = event.elementDefinition
					}
				}
				else if (isLayoutElement(event) && event.elementDefinition == event.elementDefinition) {
					insideLayoutElementDefinition = null
				}
			}
		} as IModelVisitor)

		return fragments
	}
}
