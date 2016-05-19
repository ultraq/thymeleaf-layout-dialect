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
import nz.net.ultraq.thymeleaf.models.ModelFinder

import org.thymeleaf.model.ICloseElementTag
import org.thymeleaf.model.IModel
import org.thymeleaf.model.IModelFactory
import org.thymeleaf.model.IOpenElementTag

/**
 * A decorator made to work over an HTML document.  Decoration for a document
 * involves 2 sub-decorators: a special one of the {@code <head>} element, and a
 * standard one for the {@code <body>} element.
 * 
 * @author Emanuel Rabina
 */
class HtmlDocumentDecorator implements Decorator {

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

		// TODO: Expand the model finder to locate models within models so I
		//       don't have to go through the template manager.  I think it'll
		//       also reduce the need for me to pass these template names around.

		def targetHeadModel = modelFinder.find(targetDocumentTemplate, 'head')
		new HtmlHeadDecorator(modelFactory, sortingStrategy).decorate(
			targetHeadModel, targetDocumentTemplate,
			modelFinder.find(sourceDocumentTemplate, 'head'), sourceDocumentTemplate
		)

		// Replace the head element and events with the decorated one
		// TODO: This feels pretty hacky and should be done as part of the head
		//       decorator using a structure handler or something
		def headIndex = -1
		for (def i = 0; i < targetDocumentModel.size(); i++) {
			def event = targetDocumentModel.get(i)
			if (event instanceof IOpenElementTag && event.elementCompleteName == 'head') {
				headIndex = i
				break
			}
		}
		if (headIndex > 0) {
			while (true) {
				def lastEvent = targetDocumentModel.get(headIndex)
				targetDocumentModel.remove(headIndex)
				if (lastEvent instanceof ICloseElementTag && lastEvent.elementCompleteName == 'head') {
					break;
				}
			}
			targetDocumentModel.insertModel(headIndex, targetHeadModel)
		}

		new HtmlBodyDecorator(modelFactory).decorate(
			modelFinder.find(targetDocumentTemplate, 'body'), targetDocumentTemplate,
			modelFinder.find(sourceDocumentTemplate, 'body'), sourceDocumentTemplate
		)

		// TODO
		// Set the doctype from the decorator if missing from the content page
//		def decoratorDocument = decoratorModel.parent
//		def contentDocument   = contentModel.parent
//		if (!contentDocument.docType && decoratorDocument.docType) {
//			contentDocument.docType = decoratorDocument.docType
//		}


		// Find the root element of the target document to merge
		// TODO: Way of obtaining a model from within a model
		def rootElementEventIndex = targetDocumentModel.findIndexOf { targetDocumentEvent ->
			return targetDocumentEvent instanceof IOpenElementTag
		}
		def targetDocumentRootModel = modelFinder.find(targetDocumentTemplate,
			targetDocumentModel.get(rootElementEventIndex).elementCompleteName)

		// Bring the decorator into the content page (which is the one being processed)
		new AttributeMerger(modelFactory).merge(targetDocumentRootModel, sourceDocumentModel)
		targetDocumentModel.replace(rootElementEventIndex, targetDocumentRootModel.get(0))
	}
}
