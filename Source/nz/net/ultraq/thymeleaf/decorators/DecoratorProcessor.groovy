/* 
 * Copyright 2012, Emanuel Rabina (http://www.ultraq.net.nz/)
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

package nz.net.ultraq.thymeleaf.decorators

import nz.net.ultraq.thymeleaf.decorators.html.HtmlDocumentDecorator
import nz.net.ultraq.thymeleaf.decorators.xml.XmlDocumentDecorator
import nz.net.ultraq.thymeleaf.fragments.FragmentMap
import nz.net.ultraq.thymeleaf.fragments.FragmentFinder
import nz.net.ultraq.thymeleaf.models.ModelFinder

import org.thymeleaf.context.ITemplateContext
import org.thymeleaf.engine.AttributeName
import org.thymeleaf.model.IModel
import org.thymeleaf.processor.element.AbstractAttributeModelProcessor
import org.thymeleaf.processor.element.IElementModelStructureHandler
import org.thymeleaf.templatemode.TemplateMode

/**
 * Specifies the name of the decorator template to apply to a content template.
 * <p>
 * The mechanism for resolving decorator templates is the same as that used by
 * Thymeleaf to resolve pages in the <tt>th:fragment</tt> and
 * <tt>th:include</tt> processors.
 * 
 * @author Emanuel Rabina
 */
class DecoratorProcessor extends AbstractAttributeModelProcessor {

	static final String PROCESSOR_NAME = 'decorator'
	static final int PROCESSOR_PRECEDENCE = 0

	// NOTE: Hopefully my PR gets merged so I don't have to keep this around
	//       https://github.com/thymeleaf/thymeleaf/pull/494
	private final String dialectPrefix
	private final SortingStrategy sortingStrategy

	/**
	 * Constructor, configure this processor to work on the 'decorator'
	 * attribute and to use the given sorting strategy.
	 * 
	 * @param templateMode
	 * @param dialectPrefix
	 * @param sortingStrategy
	 */
	DecoratorProcessor(TemplateMode templateMode, String dialectPrefix, SortingStrategy sortingStrategy) {

		super(templateMode, dialectPrefix, null, false, PROCESSOR_NAME, true, PROCESSOR_PRECEDENCE, true)

		this.dialectPrefix   = dialectPrefix
		this.sortingStrategy = sortingStrategy
	}

	/**
	 * Locates the decorator page specified by the layout attribute and applies
	 * it to the current page being processed.
	 * 
	 * @param context
	 * @param model
	 * @param attributeName
	 * @param attributeValue
	 * @param structureHandler
	 */
	@Override
	protected void doProcess(ITemplateContext context, IModel model, AttributeName attributeName,
		String attributeValue, IElementModelStructureHandler structureHandler) {

		// Ensure the decorator attribute is in the root element of the document
		if (context.elementStack.size() != 1) {
			throw new IllegalArgumentException('layout:decorator attribute must appear in the root element of your content page')
		}

		// Locate the template to 'redirect' processing to by completely replacing
		// the current document with it
		def modelFinder = new ModelFinder(context, templateMode)
		def decoratorTemplate = modelFinder.findTemplate(attributeValue)

		// Gather all fragment parts from this page to apply to the new document
		// after decoration has taken place
		def pageFragments = new FragmentFinder(modelFinder, dialectPrefix).findFragments(model)

		// Choose the decorator to use based on template mode, then apply it
		def decorator =
			templateMode == TemplateMode.HTML ? new HtmlDocumentDecorator(modelFinder, sortingStrategy) :
			templateMode == TemplateMode.XML  ? new XmlDocumentDecorator() :
			null
		if (!decorator) {
			throw new IllegalArgumentException("""
				Layout dialect cannot be applied to the ${templateMode} template mode,
				only HTML and XML template modes are currently supported
				""".stripMargin())
		}
		decorator.decorate(decoratorTemplate, model)

		// Replace the page contents with those of the template we're decorating
//		structureHandler.setTemplateData(decoratorTemplateModel.templateData)
//		structureHandler.setBody(decoratorTemplateModel, true)

		FragmentMap.setForNode(context, structureHandler, pageFragments)
	}
}
