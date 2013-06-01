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

package nz.net.ultraq.thymeleaf.decorator;

import nz.net.ultraq.thymeleaf.AbstractContentProcessor;

import static nz.net.ultraq.thymeleaf.LayoutDialect.LAYOUT_PREFIX;
import static nz.net.ultraq.thymeleaf.decorator.DecoratorUtilities.HTML_ELEMENT_HTML;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.Arguments;
import org.thymeleaf.Template;
import org.thymeleaf.TemplateProcessingParameters;
import org.thymeleaf.dom.Document;
import org.thymeleaf.dom.Element;
import org.thymeleaf.fragment.FragmentAndTarget;
import org.thymeleaf.processor.ProcessorResult;
import org.thymeleaf.standard.fragment.StandardFragmentProcessor;

import java.util.Map;

/**
 * Specifies the name of the decorator template to apply to a content template.
 * <p>
 * The mechanism for resolving decorator templates is the same as that used by
 * Thymeleaf to resolve <tt>th:fragment</tt> and <tt>th:substituteby</tt> pages.
 * 
 * @author Emanuel Rabina
 */
public class DecoratorProcessor extends AbstractContentProcessor {

	private static final Logger logger = LoggerFactory.getLogger(DecoratorProcessor.class);

	public static final String PROCESSOR_NAME_DECORATOR = "decorator";
	public static final String PROCESSOR_NAME_DECORATOR_FULL = LAYOUT_PREFIX + ":" + PROCESSOR_NAME_DECORATOR;

	/**
	 * Constructor, sets this processor to work on the 'decorator' attribute.
	 */
	public DecoratorProcessor() {

		super(PROCESSOR_NAME_DECORATOR);
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
		if (!(element.getParent() instanceof Document)) {
			logger.error("layout:decorator attribute must appear in the root element of your content page");
			throw new IllegalArgumentException("layout:decorator attribute must appear in the root element of your content page");
		}
		Document document = (Document)element.getParent();

		// Locate the decorator page
		FragmentAndTarget fragmentandtarget = StandardFragmentProcessor.computeStandardFragmentSpec(
				arguments.getConfiguration(), arguments, element.getAttributeValue(attributeName),
				null, null, false);
		Template decoratortemplate = arguments.getTemplateRepository().getTemplate(new TemplateProcessingParameters(
				arguments.getConfiguration(), fragmentandtarget.getTemplateName(), arguments.getContext()));
		element.removeAttribute(attributeName);

		Document decoratordocument = decoratortemplate.getDocument();
		Element decoratorrootelement = decoratordocument.getFirstElementChild();

		// Gather all fragment parts from this page and scope to the HTML element.
		// These will be used to decorate the document as Thymeleaf encounters the
		// fragment placeholders.
		Map<String,Object> fragments = findFragments(document.getElementChildren());
		if (!fragments.isEmpty()) {
			decoratorrootelement.setAllNodeLocalVariables(fragments);
		}

		// Decide which kind of decorator to apply, given the decorator page root element
		Decorator decorator = decoratorrootelement != null &&
				decoratorrootelement.getOriginalName().equals(HTML_ELEMENT_HTML) ?
						new HtmlDocumentDecorator() :
						new DocumentDecorator();

		// Perform decoration
		decorator.decorate(decoratorrootelement, element);

		return ProcessorResult.OK;
	}
}
