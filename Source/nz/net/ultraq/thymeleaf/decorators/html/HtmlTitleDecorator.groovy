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
import static nz.net.ultraq.thymeleaf.decorators.TitlePatternProcessor.CONTENT_TITLE_ATTRIBUTE
import static nz.net.ultraq.thymeleaf.decorators.TitlePatternProcessor.CONTENT_TITLE_ATTRIBUTE_UNESCAPED
import static nz.net.ultraq.thymeleaf.decorators.TitlePatternProcessor.LAYOUT_TITLE_ATTRIBUTE
import static nz.net.ultraq.thymeleaf.decorators.TitlePatternProcessor.LAYOUT_TITLE_ATTRIBUTE_UNESCAPED

import org.thymeleaf.context.ITemplateContext
import org.thymeleaf.model.IModel
import org.thymeleaf.standard.StandardDialect
import org.thymeleaf.standard.processor.StandardTextTagProcessor
import org.thymeleaf.standard.processor.StandardUtextTagProcessor
import org.unbescape.html.HtmlEscape

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

			// TODO: This title values map is being used as a way to communicate
			//       between this class and the title pattern processor, and being
			//       exposed on the Thymeleaf model as a result.  I should find a
			//       better way of passing these values around, maybe via the layout
			//       context.
			def titleValuesMap = [:]

			def extractTitle = { titleModel, titleAttributeEscaped, titleAttributeUnescaped ->
				def titleTag = titleModel?.first()
				def titleValue = titleTag?.getAttributeValue(titleAttributeEscaped) ?:
				                 titleTag?.getAttributeValue(standardDialectPrefix, StandardTextTagProcessor.ATTR_NAME)
				if (titleValue) {
					titleValuesMap << [(titleAttributeEscaped): titleValue]
				}
				else {
					titleValue = titleTag?.getAttributeValue(standardDialectPrefix, StandardUtextTagProcessor.ATTR_NAME) ?:
					             titleModel?.size() > 2 ? "'${HtmlEscape.escapeHtml5Xml(titleModel.get(1).text)}'" : null
					if (titleValue) {
						titleValuesMap << [(titleAttributeUnescaped): titleValue]
					}
				}
			}
			extractTitle(sourceTitleModel, CONTENT_TITLE_ATTRIBUTE, CONTENT_TITLE_ATTRIBUTE_UNESCAPED)
			extractTitle(targetTitleModel, LAYOUT_TITLE_ATTRIBUTE, LAYOUT_TITLE_ATTRIBUTE_UNESCAPED)

			resultTitle = new ModelBuilder(context).build {
				title([(titlePatternProcessor.attributeCompleteName): titlePatternProcessor.value] << titleValuesMap)
			}
		}
		else {
			resultTitle = new ElementMerger(context).merge(targetTitleModel, sourceTitleModel)
		}

		return resultTitle
	}
}
