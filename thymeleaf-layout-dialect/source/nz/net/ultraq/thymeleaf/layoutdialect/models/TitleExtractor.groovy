/*
 * Copyright 2024, Emanuel Rabina (http://www.ultraq.net.nz/)
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

package nz.net.ultraq.thymeleaf.layoutdialect.models

import org.thymeleaf.context.ITemplateContext
import org.thymeleaf.model.IModel
import org.thymeleaf.standard.StandardDialect
import org.thymeleaf.standard.processor.StandardTextTagProcessor
import org.thymeleaf.standard.processor.StandardUtextTagProcessor

import groovy.transform.TupleConstructor

/**
 * Utility class for extracting {@code <title>} values from templates.
 *
 * @author Emanuel Rabina
 */
@TupleConstructor(defaults = false)
class TitleExtractor {

	final ITemplateContext context

	/**
	 * Locate and extract title data from the given template model, saving it to
	 * the template context as the given context key.  This is so that it can be
	 * accessed from within the context level using that key.
	 *
	 * @param template
	 * @param contextKey
	 */
	void extract(IModel template, String contextKey) {

		// This title part already exists from a previous run, so do nothing
		if (context[contextKey]) {
			return
		}

		def titleModel = template?.findModel { event ->
			return event.isOpeningElementOf('title')
		}

		if (titleModel) {
			def titleTag = titleModel.first()
			def modelBuilder = new ModelBuilder(context)
			def standardDialectPrefix = context.getPrefixForDialect(StandardDialect)

			// Escapable title from a th:text attribute on the title tag
			if (titleTag.hasAttribute(standardDialectPrefix, StandardTextTagProcessor.ATTR_NAME)) {
				context[(contextKey)] = modelBuilder.build {
					'th:block'('th:text': titleTag.getAttributeValue(standardDialectPrefix, StandardTextTagProcessor.ATTR_NAME))
				}
			}

			// Unescaped title from a th:utext attribute on the title tag, or
			// whatever happens to be within the title tag
			else if (titleTag.hasAttribute(standardDialectPrefix, StandardUtextTagProcessor.ATTR_NAME)) {
				context[(contextKey)] = modelBuilder.build {
					'th:block'('th:utext': titleTag.getAttributeValue(standardDialectPrefix, StandardUtextTagProcessor.ATTR_NAME))
				}
			}

			// Title value exists within the <title>...</title> tags
			else {
				def titleChildrenModel = context.modelFactory.createModel()
				titleModel.childModelIterator().each { model ->
					titleChildrenModel.addModel(model)
				}
				context[(contextKey)] = titleChildrenModel
			}
		}
	}
}
