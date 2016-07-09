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
import nz.net.ultraq.thymeleaf.expressions.ExpressionProcessor
import nz.net.ultraq.thymeleaf.fragments.FragmentMap
import nz.net.ultraq.thymeleaf.fragments.FragmentFinder
import nz.net.ultraq.thymeleaf.models.TemplateModelFinder

import org.thymeleaf.context.ITemplateContext
import org.thymeleaf.engine.AttributeName
import org.thymeleaf.model.IModel
import org.thymeleaf.model.IOpenElementTag
import org.thymeleaf.processor.element.AbstractAttributeModelProcessor
import org.thymeleaf.processor.element.IElementModelStructureHandler
import org.thymeleaf.templatemode.TemplateMode

/**
 * Specifies the name of the template to decorate using the current template.
 * 
 * @author Emanuel Rabina
 */
class DecorateProcessor extends AbstractAttributeModelProcessor {

	static final String PROCESSOR_NAME = 'decorate'
	static final int PROCESSOR_PRECEDENCE = 0

	private final SortingStrategy sortingStrategy

	/**
	 * Constructor, configure this processor to work on the 'decorate' attribute
	 * and to use the given sorting strategy.
	 * 
	 * @param templateMode
	 * @param dialectPrefix
	 * @param sortingStrategy
	 */
	DecorateProcessor(TemplateMode templateMode, String dialectPrefix, SortingStrategy sortingStrategy) {

		this(templateMode, dialectPrefix, sortingStrategy, PROCESSOR_NAME)
	}

	/**
	 * Constructor, configurable processor name for the purposes of the
	 * deprecated {@code layout:decorator} alias.
	 * 
	 * @param templateMode
	 * @param dialectPrefix
	 * @param sortingStrategy
	 * @param attributeName
	 */
	protected DecorateProcessor(TemplateMode templateMode, String dialectPrefix, SortingStrategy sortingStrategy,
		String attributeName) {

		super(templateMode, dialectPrefix, null, false, attributeName, true, PROCESSOR_PRECEDENCE, true)
		this.sortingStrategy = sortingStrategy
	}

	/**
	 * Locates the template to decorate and, once decorated, inserts it into the
	 * processing chain.
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

		// Ensure the decorate attribute is in the root element of the document
		if (context.elementStack.size() != 1) {
			throw new IllegalArgumentException('layout:decorate/data-layout-decorate must appear in the root element of your template')
		}

		def templateModelFinder = new TemplateModelFinder(context)

		// Remove the layout:decorate attribute for cases when the root element is
		// also a potential fragment
		def rootElement = model.first()
		if (rootElement.hasAttribute(dialectPrefix, PROCESSOR_NAME)) {
			rootElement = context.modelFactory.removeAttribute(rootElement, dialectPrefix, PROCESSOR_NAME)
			model.replace(0, rootElement)
		}

		// Load the entirety of this template
		// TODO: Can probably find a way of preventing this double-loading for #102
		def contentTemplateName = context.templateData.template
		def contentTemplate = templateModelFinder.findTemplate(contentTemplateName).cloneModel()
		def origRootElement = contentTemplate.find { event ->
			return event instanceof IOpenElementTag
		}
		contentTemplate.replace(origRootElement.index, rootElement)

		// Locate the template to decorate
		def decorateTemplateExpression = new ExpressionProcessor(context).parseFragmentExpression(attributeValue)
		def decorateTemplate = templateModelFinder.findTemplate(decorateTemplateExpression).cloneModel()

		// Gather all fragment parts from this page to apply to the new document
		// after decoration has taken place
		def pageFragments = new FragmentFinder(dialectPrefix).findFragments(model)

		// Choose the decorator to use based on template mode, then apply it
		def decorator =
			templateMode == TemplateMode.HTML ? new HtmlDocumentDecorator(context, sortingStrategy) :
			templateMode == TemplateMode.XML  ? new XmlDocumentDecorator(context) :
			null
		if (!decorator) {
			throw new IllegalArgumentException(
				"Layout dialect cannot be applied to the ${templateMode} template mode, only HTML and XML template modes are currently supported"
			)
		}
		def resultTemplate = decorator.decorate(decorateTemplate, contentTemplate)
		model.replaceModel(0, resultTemplate)

		// Save layout fragments for use later by layout:fragment processors
		FragmentMap.setForNode(context, structureHandler, pageFragments)
	}
}
