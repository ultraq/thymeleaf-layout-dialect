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

package nz.net.ultraq.thymeleaf.layoutdialect.decorators

import nz.net.ultraq.thymeleaf.expressionprocessor.ExpressionProcessor
import nz.net.ultraq.thymeleaf.layoutdialect.decorators.html.HtmlDocumentDecorator
import nz.net.ultraq.thymeleaf.layoutdialect.decorators.xml.XmlDocumentDecorator
import nz.net.ultraq.thymeleaf.layoutdialect.fragments.FragmentFinder
import nz.net.ultraq.thymeleaf.layoutdialect.models.TemplateModelFinder

import org.thymeleaf.context.ITemplateContext
import org.thymeleaf.engine.AttributeName
import org.thymeleaf.model.IModel
import org.thymeleaf.model.IProcessableElementTag
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
	private final boolean autoHeadMerging
	private final boolean newTitleTokens

	/**
	 * Constructor, configure this processor to work on the 'decorate' attribute
	 * and to use the given sorting strategy.
	 */
	DecorateProcessor(TemplateMode templateMode, String dialectPrefix, SortingStrategy sortingStrategy,
		boolean autoHeadMerging, boolean newTitleTokens) {

		this(templateMode, dialectPrefix, sortingStrategy, autoHeadMerging, newTitleTokens, PROCESSOR_NAME)
	}

	/**
	 * Constructor, configurable processor name for the purposes of the
	 * deprecated {@code layout:decorator} alias.
	 */
	protected DecorateProcessor(TemplateMode templateMode, String dialectPrefix, SortingStrategy sortingStrategy,
		boolean autoHeadMerging, boolean newTitleTokens, String attributeName) {

		super(templateMode, dialectPrefix, null, false, attributeName, true, PROCESSOR_PRECEDENCE, false)

		this.sortingStrategy = sortingStrategy
		this.autoHeadMerging = autoHeadMerging
		this.newTitleTokens = newTitleTokens
	}

	/**
	 * Locates the template to decorate and, once decorated, inserts it into the
	 * processing chain.
	 */
	@Override
	protected void doProcess(ITemplateContext context, IModel model, AttributeName attributeName,
		String attributeValue, IElementModelStructureHandler structureHandler) {

		def templateModelFinder = new TemplateModelFinder(context)

		// Load the entirety of this template so we can access items outside of the root element
		def contentTemplateName = context.templateData.template
		def contentTemplate = templateModelFinder.findTemplate(contentTemplateName).cloneModel()

		// Check that the root element is the same as the one currently being processed
		def contentRootEvent = contentTemplate.find { event -> event instanceof IProcessableElementTag }
		def rootElement = model.first()
		if (!contentRootEvent.equalsIgnoreXmlnsAndWith(rootElement, context)) {
			throw new IllegalArgumentException('layout:decorate/data-layout-decorate must appear in the root element of your template')
		}

		// Remove the decorate processor from the root element
		if (rootElement.hasAttribute(attributeName)) {
			rootElement = context.modelFactory.removeAttribute(rootElement, attributeName)
			model.replace(0, rootElement)
		}
		contentTemplate.replaceModel(contentTemplate.findIndexOf { event -> event instanceof IProcessableElementTag }, model)

		// Locate the template to decorate
		def decorateTemplateExpression = new ExpressionProcessor(context).parseFragmentExpression(attributeValue)
		def decorateTemplate = templateModelFinder.findTemplate(decorateTemplateExpression)
		def decorateTemplateData = decorateTemplate.templateData
		decorateTemplate = decorateTemplate.cloneModel()

		// Extract titles from content and layout templates and save to the template context
//		def titleExtractor = new TitleExtractor(context, newTitleTokens)
//		titleExtractor.extract(contentTemplate, TitlePatternProcessor.CONTENT_TITLE_KEY)
//		titleExtractor.extract(decorateTemplate, TitlePatternProcessor.LAYOUT_TITLE_KEY)

		// Gather all fragment parts from this page to apply to the new document
		// after decoration has taken place
		def pageFragments = new FragmentFinder(dialectPrefix).findFragments(model)

		// Choose the decorator to use based on template mode, then apply it
		def decorator =
			templateMode == TemplateMode.HTML ?
				new HtmlDocumentDecorator(context, sortingStrategy, autoHeadMerging, newTitleTokens) :
				templateMode == TemplateMode.XML ?
					new XmlDocumentDecorator(context) :
					null
		if (!decorator) {
			throw new IllegalArgumentException(
				"Layout dialect cannot be applied to the ${templateMode} template mode, only HTML and XML template modes are currently supported"
			)
		}
		def resultTemplate = decorator.decorate(decorateTemplate, contentTemplate)
		model.replaceModel(0, resultTemplate)
		structureHandler.templateData = decorateTemplateData

		// Save layout fragments for use later by layout:fragment processors
		structureHandler.setLocalFragmentCollection(context, pageFragments, true)

		// Scope variables in fragment definition to template.  Parameters *must* be
		// named as there is no mechanism for setting their name at the target
		// layout/template.
		if (decorateTemplateExpression.hasParameters()) {
			if (decorateTemplateExpression.hasSyntheticParameters()) {
				throw new IllegalArgumentException('Fragment parameters must be named when used with layout:decorate/data-layout-decorate')
			}
			decorateTemplateExpression.parameters.each { parameter ->
				structureHandler.setLocalVariable(parameter.left.execute(context), parameter.right.execute(context))
			}
		}
	}
}
