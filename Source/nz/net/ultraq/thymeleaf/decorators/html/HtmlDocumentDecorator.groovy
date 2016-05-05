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
import nz.net.ultraq.thymeleaf.models.ModelFinder

import org.thymeleaf.model.IModel
import org.thymeleaf.model.IModelFactory

/**
 * A decorator made to work over an HTML document.  Decoration for a document
 * involves 2 sub-decorators: a special one of the {@code <head>} element, and a
 * standard one for the {@code <body>} element.
 * 
 * @author Emanuel Rabina
 */
class HtmlDocumentDecorator extends XmlDocumentDecorator {

	private final IModelFactory modelFactory
	private final ModelFinder modelFinder
	private final SortingStrategy sortingStrategy

	/**
	 * Constructor, apply the given sorting strategy to the decorator.
	 * 
	 * @param modelFactory
	 * @param modelFinder
	 * @param sortingStrategy
	 */
	HtmlDocumentDecorator(IModelFactory modelFactory, ModelFinder modelFinder, SortingStrategy sortingStrategy) {

		this.modelFactory    = modelFactory
		this.modelFinder     = modelFinder
		this.sortingStrategy = sortingStrategy
	}

	/**
	 * Decorate an entire HTML page.
	 * 
	 * @param targetDocumentModel
	 * @param targetDocumentTemplate
	 * @param sourceDocumentModel
	 * @param sourceDocumentTemplate
	 */
	@Override
	void decorate(IModel targetDocumentModel, String targetDocumentTemplate,
		IModel sourceDocumentModel, String sourceDocumentTemplate) {

		// TODO
//		new HtmlHeadDecorator(sortingStrategy).decorate(decoratorModel, contentModel.findElement('head'))

		new HtmlBodyDecorator(modelFactory).decorate(
			// TODO: Expand the model finder to locate models within models so I
			//       don't have to go through the template manager.  I think it'll
			//       also reduce the need for me to pass these template names around.
			modelFinder.find(targetDocumentTemplate, 'body'), targetDocumentTemplate,
			modelFinder.find(sourceDocumentTemplate, 'body'), sourceDocumentTemplate)

		// TODO
		// Set the doctype from the decorator if missing from the content page
//		def decoratorDocument = decoratorModel.parent
//		def contentDocument   = contentModel.parent
//		if (!contentDocument.docType && decoratorDocument.docType) {
//			contentDocument.docType = decoratorDocument.docType
//		}

		super.decorate(targetDocumentModel, targetDocumentTemplate, sourceDocumentModel, sourceDocumentTemplate)
	}
}
