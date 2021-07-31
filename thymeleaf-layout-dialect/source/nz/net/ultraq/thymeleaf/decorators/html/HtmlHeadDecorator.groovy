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

import groovy.transform.TupleConstructor

/**
 * A decorator specific to processing an HTML {@code <head>} element.
 * 
 * @author Emanuel Rabina
 */
@TupleConstructor(defaults = false)
class HtmlHeadDecorator implements Decorator {

	final ITemplateContext context
	final SortingStrategy sortingStrategy

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

		// New head model based off the target being decorated
		def resultHeadModel = new AttributeMerger(context).merge(targetHeadModel, sourceHeadModel)
		if (sourceHeadModel && targetHeadModel) {
			sourceHeadModel.childModelIterator().each { model ->
				resultHeadModel.insertModelWithWhitespace(
					sortingStrategy.findPositionForModel(resultHeadModel, model),
					model, modelFactory)
			}
		}

		// Replace <title>s in the result with a proper merge of the source and target <title> elements
		def indexOfTitle = resultHeadModel.findIndexOf { event -> event.isOpeningElementOf('title') }
		if (indexOfTitle != -1) {
			resultHeadModel.removeAllModels { event -> !event.whitespace && event.isOpeningElementOf('title') }
			def resultTitle = new HtmlTitleDecorator(context).decorate(
				targetHeadModel?.findModel { event -> event.isOpeningElementOf('title') },
				sourceHeadModel?.findModel { event -> event.isOpeningElementOf('title') }
			)
			resultHeadModel.insertModelWithWhitespace(indexOfTitle, resultTitle, modelFactory)
		}

		return resultHeadModel
	}
}
