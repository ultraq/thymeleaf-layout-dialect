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

import org.thymeleaf.context.ITemplateContext
import org.thymeleaf.model.ICloseElementTag
import org.thymeleaf.model.IComment
import org.thymeleaf.model.IDocType
import org.thymeleaf.model.IModel
import org.thymeleaf.model.IOpenElementTag

/**
 * A decorator made to work over an XML document.
 * 
 * @author Emanuel Rabina
 */
class XmlDocumentDecorator implements Decorator {

	protected final ITemplateContext context

	/**
	 * Constructor, set up the document decorator context.
	 * 
	 * @param context
	 */
	XmlDocumentDecorator(ITemplateContext context) {

		this.context = context
	}

	/**
	 * Decorates the target XML document with the source one.
	 * 
	 * @param targetDocumentModel
	 * @param sourceDocumentModel
	 * @return Result of the decoration.
	 */
	@Override
	IModel decorate(IModel targetDocumentModel, IModel sourceDocumentModel) {

		def modelFactory = context.modelFactory

		// Find the root element of each document to work with
		def rootModelFinder = { documentModel ->
			return documentModel.findModel { documentEvent ->
				return documentEvent instanceof IOpenElementTag
			}
		}

		// Decorate the target document with the source one
		def resultDocumentModel = new AttributeMerger(context).merge(
			rootModelFinder(targetDocumentModel),
			rootModelFinder(sourceDocumentModel)
		)

		def documentContainsDocType = { IModel document ->
			for (def i = 0; i < document.size(); i++) {
				def event = document.get(i)
				if (event instanceof IDocType) {
					return true
				}
				if (event instanceof IOpenElementTag) {
					break
				}
			}
			return false
		}

		// Copy certain items outside of the root element
		for (def i = 0; i < targetDocumentModel.size(); i++) {
			def event = targetDocumentModel.get(i)

			// Only copy doctypes if the source document doesn't already have one
			if (event instanceof IDocType) {
				if (!documentContainsDocType(sourceDocumentModel)) {
					resultDocumentModel.insertWithWhitespace(0, event, modelFactory)
				}
			}
			else if (event instanceof IComment) {
				resultDocumentModel.insertWithWhitespace(0, event, modelFactory)
			}
			else if (event instanceof IOpenElementTag) {
				break
			}
		}
		for (def i = targetDocumentModel.size() - 1; i >= 0; i--) {
			def event = targetDocumentModel.get(i)
			if (event instanceof IComment) {
				resultDocumentModel.insertWithWhitespace(resultDocumentModel.size(), event, modelFactory)
			}
			else if (event instanceof ICloseElementTag) {
				break
			}
		}

		return resultDocumentModel
	}
}
