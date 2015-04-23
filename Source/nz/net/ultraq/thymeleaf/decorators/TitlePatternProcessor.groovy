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

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.thymeleaf.Arguments
import org.thymeleaf.dom.Element
import org.thymeleaf.dom.Text
import org.thymeleaf.exceptions.TemplateProcessingException
import org.thymeleaf.processor.ProcessorResult
import org.thymeleaf.processor.attr.AbstractAttrProcessor
import org.thymeleaf.standard.expression.StandardExpressions

/**
 * Allows for greater control of the resulting &lttitle&gt element by
 * specifying a pattern with some special tokens.  This can be used to extend
 * the decorator's title with the content's one, instead of simply overriding
 * it.
 * 
 * @author Emanuel Rabina
 */
class TitlePatternProcessor extends AbstractAttrProcessor {

	private static final Logger logger = LoggerFactory.getLogger(TitlePatternProcessor)

	private static final String PARAM_TITLE_DECORATOR = '$DECORATOR_TITLE'
	private static final String PARAM_TITLE_CONTENT   = '$CONTENT_TITLE'

	static final String PROCESSOR_NAME_TITLEPATTERN = 'title-pattern'

	static final String DECORATOR_TITLE = 'title-pattern::decorator-title'
	static final String CONTENT_TITLE   = 'title-pattern::content-title'

	final int precedence = 1

	/**
	 * Constructor, sets this processor to work on the 'title-pattern' attribute.
	 */
	TitlePatternProcessor() {

		super(PROCESSOR_NAME_TITLEPATTERN)
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected ProcessorResult processAttribute(Arguments arguments, Element element, String attributeName) {

		// Ensure this attribute is only on the <title> element
		if (element.normalizedName != 'title') {
			def message = 'layout:title-pattern attribute should only appear in a <title> element'
			logger.error(message)
			throw new IllegalArgumentException(message)
		}

		def configuration = arguments.configuration
		def parser = StandardExpressions.getExpressionParser(configuration)

		// Process the decorator and content title parts
		def processTitle = { title ->
			try {
				return title ?
					parser.parseExpression(configuration, arguments, title)
						.execute(configuration, arguments)
						.toString()?.trim() :
					null
			}
			catch (TemplateProcessingException ex) {
				return title?.trim()
			}
		}
		def decoratorTitle = processTitle(element.getNodeProperty(DECORATOR_TITLE))
		def contentTitle = processTitle(element.getNodeProperty(CONTENT_TITLE))

		// Replace the <title> text with an expanded title pattern expression,
		// only using the pattern if both the decorator and content have a title.
		def titlePattern = element.getAttributeValue(attributeName)
		def title = decoratorTitle && contentTitle ?
			titlePattern
				.replace(PARAM_TITLE_DECORATOR, decoratorTitle)
				.replace(PARAM_TITLE_CONTENT, contentTitle) :
			decoratorTitle ?: contentTitle ?: ''

		element.clearChildren()
		element.addChild(new Text(title))

		element.removeAttribute(attributeName)
		return ProcessorResult.OK
	}
}
