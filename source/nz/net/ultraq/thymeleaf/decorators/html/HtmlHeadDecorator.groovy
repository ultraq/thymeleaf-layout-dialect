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

import nz.net.ultraq.thymeleaf.decorators.Decorator
import nz.net.ultraq.thymeleaf.decorators.SortingStrategy
import nz.net.ultraq.thymeleaf.models.AttributeMerger

import org.thymeleaf.context.ITemplateContext
import org.thymeleaf.model.IModel
import org.thymeleaf.model.IOpenElementTag

/**
 * A decorator specific to processing an HTML {@code <head>} element.
 * 
 * @author Emanuel Rabina
 */
class HtmlHeadDecorator implements Decorator {

	private final ITemplateContext context
	private final SortingStrategy sortingStrategy

	/**
	 * Constructor, sets up the decorator context.
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

		// If none of the parameters are present, return nothing
		if (!targetHeadModel && !sourceHeadModel) {
			return null
		}

		def modelFactory = context.modelFactory
		def isTitle = { event -> event instanceof IOpenElementTag && event.elementCompleteName == 'title' }

		// New head model based off the target being decorated
		def resultHeadModel = new AttributeMerger(context).merge(targetHeadModel, sourceHeadModel)
		def titleIndex = resultHeadModel.findIndexOf(isTitle)
		if (titleIndex != -1) {
			resultHeadModel.removeModelWithWhitespace(titleIndex)
		}

		// Get the source and target title elements to pass to the title decorator
		def titleRetriever = { headModel -> headModel?.findModel(isTitle) }
		def resultTitle = new HtmlTitleDecorator(context).decorate(
			titleRetriever(targetHeadModel),
			titleRetriever(sourceHeadModel)
		)
		resultHeadModel.insertModelWithWhitespace(1, resultTitle, modelFactory)

		// Merge the rest of the source <head> elements with the target <head>
		// elements using the current merging strategy
		if (sourceHeadModel && targetHeadModel) {
			sourceHeadModel.childModelIterator()
				.findAll { model -> !isTitle(model.first()) }
				.each { model ->
					def position = sortingStrategy.findPositionForModel(resultHeadModel, model)
					if (position != -1) {
						resultHeadModel.insertModelWithWhitespace(position, model, modelFactory)
					}
				}
		}

		return resultHeadModel
	}
}
