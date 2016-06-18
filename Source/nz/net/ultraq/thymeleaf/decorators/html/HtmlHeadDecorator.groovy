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

import nz.net.ultraq.thymeleaf.LayoutDialect
import nz.net.ultraq.thymeleaf.decorators.Decorator
import nz.net.ultraq.thymeleaf.decorators.SortingStrategy
import nz.net.ultraq.thymeleaf.decorators.TitlePatternProcessor
import nz.net.ultraq.thymeleaf.models.AttributeMerger
import nz.net.ultraq.thymeleaf.models.ElementMerger
import nz.net.ultraq.thymeleaf.models.ModelBuilder

import org.thymeleaf.context.ITemplateContext
import org.thymeleaf.model.IModel
import org.thymeleaf.model.IOpenElementTag
import org.thymeleaf.standard.StandardDialect
import org.thymeleaf.standard.processor.StandardTextTagProcessor

/**
 * A decorator specific to processing an HTML {@code <head>} element.
 * 
 * @author Emanuel Rabina
 */
class HtmlHeadDecorator implements Decorator {

	private static final Closure TITLE_EVENT_INDEX_FINDER = { event ->
		return event instanceof IOpenElementTag && event.elementCompleteName == 'title'
	}

	private final ITemplateContext context
	private final SortingStrategy sortingStrategy

	/**
	 * Constructor, sets up the element decorator context.
	 * 
	 * @param context
	 * @param sortingStrategy
	 */
	HtmlHeadDecorator(ITemplateContext context, SortingStrategy sortingStrategy) {

		this.context         = context
		this.sortingStrategy = sortingStrategy
	}

	/**
	 * Decorate the {@code <head>} part.
	 * 
	 * @param targetHeadModel
	 * @param sourceHeadModel
	 * @return Result of the decoration.
	 */
	@Override
	IModel decorate(IModel targetHeadModel, IModel sourceHeadModel) {

		// If one of the parameters is missing return a copy of the other, or
		// nothing if both parameters are missing.
		if (!targetHeadModel || !sourceHeadModel) {
			return targetHeadModel ? targetHeadModel.cloneModel() : sourceHeadModel ? sourceHeadModel.cloneModel() : null
		}

		// Get the source and target title elements
		def sourceTitle = sourceHeadModel.findModel(TITLE_EVENT_INDEX_FINDER)
		if (sourceTitle) {
			sourceHeadModel.removeModelWithWhitespace(sourceTitle.index)
		}
		def targetTitle = targetHeadModel.findModel(TITLE_EVENT_INDEX_FINDER)
		if (targetTitle) {
			targetHeadModel.removeModelWithWhitespace(targetTitle.index)
		}

		def titlePatternProcessorRetriever = { titleModel ->
			return titleModel?.first()?.getAttribute(LayoutDialect.DIALECT_PREFIX, TitlePatternProcessor.PROCESSOR_NAME)
		}
		def titlePatternProcessor =
			titlePatternProcessorRetriever(sourceTitle) ?:
			titlePatternProcessorRetriever(targetTitle) ?:
			null
		def resultTitle
		if (titlePatternProcessor) {
			def titleValueRetriever = { titleModel ->
				return titleModel.first().getAttributeValue(StandardDialect.PREFIX, StandardTextTagProcessor.ATTR_NAME) ?:
					titleModel.size() > 2 ? "'${titleModel.get(1).text}'" : null
			}
			def contentTitle = titleValueRetriever(sourceTitle)
			def decoratorTitle = titleValueRetriever(targetTitle)

			resultTitle = new ModelBuilder(context).build {
				title([
					(titlePatternProcessor.attributeCompleteName): titlePatternProcessor.value,
					'data-layout-content-title': contentTitle,
					'data-layout-decorator-title': decoratorTitle
				])
			}
		}
		else {
			resultTitle = new ElementMerger(context.modelFactory).merge(targetTitle, sourceTitle)
		}

		targetHeadModel.insertModelWithWhitespace(1, resultTitle)

//		def titleContainer = modelBuilder.build {
//			'title-container'(
//				titlePatternProcessor ? [(titlePatternProcessor.attributeCompleteName): titlePatternProcessor.value] : [:]) {
//				if (sourceTitle) {
//					add(sourceTitle)
//				}
//				if (targetTitle) {
//					add(targetTitle)
//				}
//			}
//		}

/*
		// Copy the content and decorator <title>s
		// TODO: Surely the code below can be simplified?  The 2 conditional
		//       blocks are doing almost the same thing.
		def titleContainer = new Element('title-container')
		def titlePattern = null
		def titleExtraction = { headElement, titleType ->
			def existingContainer = headElement?.findElement('title-container')
			if (existingContainer) {
				def titleElement = existingContainer.children.last()
				titlePattern = titleElement.getAttributeValue(DIALECT_PREFIX_LAYOUT, PROCESSOR_NAME) ?: titlePattern
				titleElement.setNodeProperty(TITLE_TYPE, titleType)
				headElement.removeChildWithWhitespace(existingContainer)
				titleContainer.addChild(existingContainer)
			}
			else {
				def titleElement = headElement?.findElement('title')
				if (titleElement) {
					titlePattern = titleElement.getAttributeValue(DIALECT_PREFIX_LAYOUT, PROCESSOR_NAME) ?: titlePattern
					titleElement.setNodeProperty(TITLE_TYPE, titleType)
					titleElement.removeAttribute(DIALECT_PREFIX_LAYOUT, PROCESSOR_NAME)
					headElement.removeChildWithWhitespace(titleElement)
					titleContainer.addChild(titleElement)
				}
			}
		}
		titleExtraction(decoratorHead, TITLE_TYPE_DECORATOR)
		titleExtraction(contentHead, TITLE_TYPE_CONTENT)

		def resultTitle = new Element('title')
		resultTitle.setAttribute("${DIALECT_PREFIX_LAYOUT}:${PROCESSOR_NAME}", titlePattern)
		titleContainer.addChild(resultTitle)
*/
		// Merge the source <head> elements with the target <head> elements using
		// the current merging strategy, placing the resulting title at the
		// beginning of it
		if (sourceHeadModel) {
			sourceHeadModel.childModelIterator().each { childModel ->
				def position = sortingStrategy.findPositionForModel(targetHeadModel, childModel)
				if (position != -1) {
					targetHeadModel.insertModelWithWhitespace(position, childModel)
				}
			}
		}

		return new AttributeMerger(context.modelFactory).merge(targetHeadModel, sourceHeadModel)
	}
}
