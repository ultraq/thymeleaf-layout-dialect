/* 
 * Copyright 2016, Emanuel Rabina (http://www.ultraq.net.nz/)
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

import nz.net.ultraq.thymeleaf.LayoutDialect
import nz.net.ultraq.thymeleaf.decorators.Decorator
import nz.net.ultraq.thymeleaf.decorators.TitlePatternProcessor
import nz.net.ultraq.thymeleaf.models.ElementMerger
import nz.net.ultraq.thymeleaf.models.ModelBuilder

import org.thymeleaf.context.ITemplateContext
import org.thymeleaf.model.IModel
import org.thymeleaf.standard.StandardDialect
import org.thymeleaf.standard.processor.StandardTextTagProcessor
import org.thymeleaf.standard.processor.StandardUtextTagProcessor

/**
 * Decorator for the {@code <title>} part of the template to handle the special
 * processing required for the {@code layout:title-pattern} processor.
 * 
 * @author Emanuel Rabina
 */
class HtmlTitleDecorator implements Decorator {

	private final ITemplateContext context

	/**
	 * Constructor, sets up the decorator context.
	 * 
	 * @param context
	 * @param sortingStrategy
	 */
	HtmlTitleDecorator(ITemplateContext context) {

		this.context = context
	}

	/**
	 * Special decorator for the {@code <title>} part, accumulates the important
	 * processing parts for the {@code layout:title-pattern} processor.
	 * 
	 * @param targetTitleModel
	 * @param sourceTitleModel
	 * @return A new {@code <title>} model that is the result of decorating the
	 *   {@code <title>}s.
	 */
	@Override
	@SuppressWarnings('SpaceAroundOperator')
	IModel decorate(IModel targetTitleModel, IModel sourceTitleModel) {

		def modelBuilder = new ModelBuilder(context)
		def layoutDialectPrefix = context.getPrefixForDialect(LayoutDialect)
		def standardDialectPrefix = context.getPrefixForDialect(StandardDialect)

		// Get the title pattern to use
		def titlePatternProcessorRetriever = { titleModel ->
			return titleModel?.first()?.getAttribute(layoutDialectPrefix, TitlePatternProcessor.PROCESSOR_NAME)
		}
		def titlePatternProcessor =
			titlePatternProcessorRetriever(sourceTitleModel) ?:
			titlePatternProcessorRetriever(targetTitleModel) ?:
			null

		def resultTitle

		// Set the title pattern to use on a new model, as well as the important
		// title result parts that we want to use on the pattern.
		if (titlePatternProcessor) {
			def extractTitle = { titleModel, contextKey ->

				// This title part already exists from a previous run, so do nothing
				if (context[contextKey]) {
					return
				}

				if (titleModel) {
					def titleTag = titleModel.first()

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
					else {
						def titleChildrenModel = context.modelFactory.createModel()
						titleModel.childModelIterator().each { model ->
							titleChildrenModel.addModel(model)
						}
						context[(contextKey)] = titleChildrenModel
					}
				}
			}
			extractTitle(sourceTitleModel, TitlePatternProcessor.CONTENT_TITLE_KEY)
			extractTitle(targetTitleModel, TitlePatternProcessor.LAYOUT_TITLE_KEY)

			resultTitle = new ModelBuilder(context).build {
				title((titlePatternProcessor.attributeCompleteName): titlePatternProcessor.value)
			}
		}
		else {
			resultTitle = new ElementMerger(context).merge(targetTitleModel, sourceTitleModel)
		}

		return resultTitle
	}
}
