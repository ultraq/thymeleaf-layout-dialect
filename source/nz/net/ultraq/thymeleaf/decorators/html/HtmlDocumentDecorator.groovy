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
import nz.net.ultraq.thymeleaf.decorators.xml.XmlDocumentDecorator

import org.thymeleaf.context.ITemplateContext
import org.thymeleaf.model.ICloseElementTag
import org.thymeleaf.model.IModel
import org.thymeleaf.model.IOpenElementTag

/**
 * A decorator made to work over an HTML document.  Decoration for a document
 * involves 2 sub-decorators: a special one for the {@code <head>} element, and
 * a standard one for the {@code <body>} element.
 * 
 * @author Emanuel Rabina
 */
class HtmlDocumentDecorator extends XmlDocumentDecorator {

	private final SortingStrategy sortingStrategy

	/**
	 * Constructor, apply the given sorting strategy to the decorator.
	 * 
	 * @param context
	 * @param sortingStrategy
	 */
	HtmlDocumentDecorator(ITemplateContext context, SortingStrategy sortingStrategy) {

		super(context)
		this.sortingStrategy = sortingStrategy
	}

	/**
	 * Decorate an entire HTML page.
	 * 
	 * @param targetDocumentModel
	 * @param sourceDocumentModel
	 * @return Result of the decoration.
	 */
	@Override
	IModel decorate(IModel targetDocumentModel, IModel sourceDocumentModel) {

		def modelFactory = context.modelFactory
		def resultDocumentModel = targetDocumentModel.cloneModel()

		// Head decoration
		def headModelFinder = { event ->
			return event instanceof IOpenElementTag && event.elementCompleteName == 'head'
		}
		def targetHeadModel = resultDocumentModel.findModel(headModelFinder)
		def resultHeadModel = new HtmlHeadDecorator(context, sortingStrategy).decorate(
			targetHeadModel,
			sourceDocumentModel.findModel(headModelFinder)
		)
		if (resultHeadModel) {
			if (targetHeadModel) {
				resultDocumentModel.replaceModel(resultDocumentModel.indexOf(targetHeadModel), resultHeadModel)
			}
			else {
				resultDocumentModel.insertModelWithWhitespace(resultDocumentModel.findIndexOf { event ->
					return (event instanceof IOpenElementTag && event.elementCompleteName == 'body') ||
					       (event instanceof ICloseElementTag && event.elementCompleteName == 'html')
				} - 1, resultHeadModel, modelFactory)
			}
		}

		// Body decoration
		def bodyModelFinder = { event ->
			return event instanceof IOpenElementTag && event.elementCompleteName == 'body'
		}
		def targetBodyModel = resultDocumentModel.findModel(bodyModelFinder)
		def resultBodyModel = new HtmlBodyDecorator(context).decorate(
			targetBodyModel,
			sourceDocumentModel.findModel(bodyModelFinder)
		)
		if (resultBodyModel) {
			if (targetBodyModel) {
				resultDocumentModel.replaceModel(resultDocumentModel.indexOf(targetBodyModel), resultBodyModel)
			}
			else {
				resultDocumentModel.insertModelWithWhitespace(resultDocumentModel.findIndexOf { event ->
					return event instanceof ICloseElementTag && event.elementCompleteName == 'html'
				} - 1, resultBodyModel, modelFactory)
			}
		}

		return super.decorate(resultDocumentModel, sourceDocumentModel)
	}
}
