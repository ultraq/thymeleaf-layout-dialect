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
import nz.net.ultraq.thymeleaf.fragments.FragmentLocator
import static nz.net.ultraq.thymeleaf.fragments.FragmentProcessor.PROCESSOR_NAME_FRAGMENT
import static nz.net.ultraq.thymeleaf.LayoutDialect.DIALECT_PREFIX_LAYOUT
import static nz.net.ultraq.thymeleaf.utilities.LayoutUtilities.*

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.thymeleaf.Arguments
import org.thymeleaf.TemplateProcessingParameters
import org.thymeleaf.dom.Document
import org.thymeleaf.dom.Element
import org.thymeleaf.processor.ProcessorResult
import org.thymeleaf.processor.attr.AbstractAttrProcessor
import org.thymeleaf.standard.fragment.StandardFragmentProcessor

/**
 * Specifies the name of the decorator template to apply to a content template.
 * <p>
 * The mechanism for resolving decorator templates is the same as that used by
 * Thymeleaf to resolve pages in the <tt>th:fragment</tt> and
 * <tt>th:include</tt> processors.
 * 
 * @author Emanuel Rabina
 */
class DecoratorProcessor extends AbstractAttrProcessor {

	private static final Logger logger = LoggerFactory.getLogger(DecoratorProcessor)
	private static final String TEMPLATE_MODE_LEGACYHTML5 = 'LEGACYHTML5'

	static final String PROCESSOR_NAME_DECORATOR = 'decorator'

	final int precedence = 0

	/**
	 * Constructor, sets this processor to work on the 'decorator' attribute.
	 */
	DecoratorProcessor() {

		super(PROCESSOR_NAME_DECORATOR)
	}

	/**
	 * Locates the decorator page specified by the layout attribute and applies
	 * it to the current page being processed.
	 * 
	 * @param arguments
	 * @param element
	 * @param attributeName
	 * @return Result of the processing.
	 */
	@Override
	protected ProcessorResult processAttribute(Arguments arguments, Element element, String attributeName) {

		// Ensure the decorator attribute is in the root element of the document
		// NOTE: The NekoHTML parser adds <html> and <body> elements to template
		//       fragments that don't already have them, potentially failing
		//       this restriction.  For now I'll relax it for the LEGACYHTML5
		//       template mode, but developers should be aware that putting the
		//       layout:decorator attribute anywhere but the root element can
		//       lead to unexpected results.
		if (!(element.parent instanceof Document) &&
			!arguments.templateResolution.templateMode == TEMPLATE_MODE_LEGACYHTML5) {
			logger.error('layout:decorator attribute must appear in the root element of your content page')
			throw new IllegalArgumentException('layout:decorator attribute must appear in the root element of your content page')
		}

		def document = arguments.document

		// Locate the decorator page
		def fragment = StandardFragmentProcessor.computeStandardFragmentSpec(
				arguments.configuration, arguments, element.getAttributeValue(attributeName),
				DIALECT_PREFIX_LAYOUT, PROCESSOR_NAME_FRAGMENT)
		def decoratorTemplate = arguments.templateRepository.getTemplate(new TemplateProcessingParameters(
				arguments.configuration, fragment.templateName, arguments.context))

		def decoratorDocument = decoratorTemplate.document
		def decoratorRootElement = decoratorDocument.firstElementChild

		// Gather all fragment parts from this page
		def fragments = new FragmentLocator().locate(document.elementChildren)

		// Decide which kind of decorator to apply, given the decorator page root element
		def decorator = decoratorRootElement?.originalName == HTML_ELEMENT_HTML ?
				new HtmlDocumentDecorator() :
				new XmlDocumentDecorator()

		// Perform decoration, apply any new fragments found from the pre-decorated page
		decorator.decorate(decoratorRootElement, document.firstElementChild)
		if (fragments) {
			def newRootElement = document.firstElementChild
			def nodeLocalVariables = newRootElement.nodeLocalVariableNames
			newRootElement.setAllNodeLocalVariables(fragments.findAll { key, value ->
				return !nodeLocalVariables.contains(key)
			})
		}

		element.removeAttribute(attributeName)
		return ProcessorResult.OK
	}
}
