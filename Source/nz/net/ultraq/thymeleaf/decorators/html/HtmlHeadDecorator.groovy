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

import nz.net.ultraq.thymeleaf.decorators.SortingStrategy
import nz.net.ultraq.thymeleaf.decorators.xml.XmlElementDecorator

import org.thymeleaf.model.IModel
import org.thymeleaf.model.IModelFactory
import org.thymeleaf.model.IOpenElementTag

/**
 * A decorator specific to processing an HTML {@code <head>} element.
 * 
 * @author Emanuel Rabina
 */
class HtmlHeadDecorator extends XmlElementDecorator {

	private final SortingStrategy sortingStrategy

	/**
	 * Constructor, sets up the element decorator context.
	 *
	 * @param modelFactory
	 * @param sortingStrategy
	 */
	HtmlHeadDecorator(IModelFactory modelFactory, SortingStrategy sortingStrategy) {

		super(modelFactory)
		this.sortingStrategy = sortingStrategy
	}

	/**
	 * Decorate the {@code <head>} part.
	 * 
	 * @param targetHeadModel
	 * @param targetHeadTemplate
	 * @param sourceHeadModel
	 * @param sourceHeadTemplate
	 */
	@Override
	void decorate(IModel targetHeadModel, String targetHeadTemplate,
		IModel sourceHeadModel, String sourceHeadTemplate) {

		// Try to ensure there is a head as a result of decoration, applying the
		// source head, or just using what is in the target
		if (!targetHeadModel.hasContent()) {
			if (sourceHeadModel.hasContent()) {
				targetHeadModel.replaceModel(sourceHeadModel)
			}
			return
		}

		// Replace the target title with the source one if present
		def titleEventIndexFinder = { event ->
			return event instanceof IOpenElementTag && event.elementCompleteName == 'title'
		}

		def sourceTitle;
		def sourceTitleIndex = sourceHeadModel.findIndexOf(titleEventIndexFinder)
		if (sourceTitleIndex != -1) {
			sourceTitle = sourceHeadModel.getModel(sourceTitleIndex)
			sourceHeadModel.removeModel(sourceTitleIndex)

			def targetTitleIndex = targetHeadModel.findIndexOf(titleEventIndexFinder)
			if (targetTitleIndex != -1) {
				targetHeadModel.removeModel(targetTitleIndex)
			}

			targetHeadModel.insertModelWithWhitespace(1, sourceTitle)
		}

		// TODO: complicated title replacement
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
		if (sourceHeadModel.hasContent()) {
			sourceHeadModel.modelIterator().each { sourceHeadSubModel ->
				def position = sortingStrategy.findPositionForModel(targetHeadModel, sourceHeadSubModel)
				if (position != -1) {
					targetHeadModel.insertModelWithWhitespace(position, sourceHeadSubModel)
				}
			}
		}

		super.decorate(targetHeadModel, targetHeadTemplate, sourceHeadModel, sourceHeadTemplate)
	}
}
