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

package nz.net.ultraq.thymeleaf.decorators.xml

import nz.net.ultraq.thymeleaf.decorators.Decorator
import nz.net.ultraq.thymeleaf.models.AttributeMerger
import nz.net.ultraq.thymeleaf.models.ModelFinder

import org.thymeleaf.model.IModel
import org.thymeleaf.model.IModelFactory
import org.thymeleaf.model.IOpenElementTag

/**
 * A decorator made to work over an XML document.
 * 
 * @author Emanuel Rabina
 */
class XmlDocumentDecorator implements Decorator {

	protected final IModelFactory modelFactory
	protected final ModelFinder modelFinder

	/**
	 * Constructor, set up the document decorator context.
	 * 
	 * @param modelFactory
	 * @param modelFinder
	 */
	XmlDocumentDecorator(IModelFactory modelFactory, ModelFinder modelFinder) {

		this.modelFactory = modelFactory
		this.modelFinder  = modelFinder
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void decorate(IModel targetDocument, String targetDocumentTemplate,
		IModel sourceDocument, String sourceDocumentTemplate) {

		// TODO
		// Copy text outside of the root element, keeping whitespace copied to a minimum
//		def beforeHtml = true
//		def allowNext = false
//		def lastNode = contentXml
//		decoratorDocument.children.each { externalNode ->
//			if (externalNode == decoratorXml) {
//				beforeHtml = false
//				allowNext = true
//				return
//			}
//			if (externalNode instanceof Comment || allowNext) {
//				if (beforeHtml) {
//					contentDocument.insertBefore(contentXml, externalNode)
//				}
//				else {
//					contentDocument.insertAfter(lastNode, externalNode)
//					lastNode = externalNode
//				}
//				allowNext = externalNode instanceof Comment
//			}
//		}

		// Find the root element of the target document to merge
		// TODO: Way of obtaining a model from within a model
		def targetDocumentRootElement = targetDocument.find { targetDocumentEvent ->
			return targetDocumentEvent instanceof IOpenElementTag
		}
		def targetDocumentRootModel = modelFinder.find(targetDocumentTemplate, targetDocumentRootElement.elementCompleteName)

		// Bring the decorator into the content page (which is the one being processed)
		new AttributeMerger(modelFactory).merge(targetDocumentRootModel, sourceDocument)
		targetDocument.replaceModel(targetDocumentRootModel)
	}
}
