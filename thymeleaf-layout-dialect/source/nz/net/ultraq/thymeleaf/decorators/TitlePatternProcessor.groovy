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

import org.thymeleaf.context.ITemplateContext
import org.thymeleaf.engine.AttributeName
import org.thymeleaf.model.IProcessableElementTag
import org.thymeleaf.processor.element.AbstractAttributeTagProcessor
import org.thymeleaf.processor.element.IElementTagStructureHandler
import org.thymeleaf.templatemode.TemplateMode

import java.util.regex.Pattern

/**
 * Allows for greater control of the resulting {@code <title>} element by
 * specifying a pattern with some special tokens.  This can be used to extend
 * the layout's title with the content's one, instead of simply overriding
 * it.
 * 
 * @author Emanuel Rabina
 */
class TitlePatternProcessor extends AbstractAttributeTagProcessor {

//	private static final String TOKEN_CONTENT_TITLE   = '$CONTENT_TITLE'
	private static final String TOKEN_LAYOUT_TITLE    = '$LAYOUT_TITLE'
	private static final Pattern TOKEN_PATTERN = ~/(\$(LAYOUT|CONTENT)_TITLE)/

	static final String PROCESSOR_NAME = 'title-pattern'
	static final int PROCESSOR_PRECEDENCE = 1

	static final String CONTENT_TITLE_KEY = 'LayoutDialect::ContentTitle'
	static final String LAYOUT_TITLE_KEY  = 'LayoutDialect::LayoutTitle'

	/**
	 * Constructor, sets this processor to work on the 'title-pattern' attribute.
	 * 
	 * @param templateMode
	 * @param dialectPrefix
	 */
	TitlePatternProcessor(TemplateMode templateMode, String dialectPrefix) {

		super(templateMode, dialectPrefix, null, false, PROCESSOR_NAME, true, PROCESSOR_PRECEDENCE, true)
	}

	/**
	 * Process the {@code layout:title-pattern} directive, replaces the title text
	 * with the titles from the content and layout pages.
	 * 
	 * @param context
	 * @param tag
	 * @param attributeName
	 * @param attributeValue
	 * @param structureHandler
	 */
	@Override
	@SuppressWarnings('AssignmentToStaticFieldFromInstanceMethod')
	protected void doProcess(ITemplateContext context, IProcessableElementTag tag,
		AttributeName attributeName, String attributeValue, IElementTagStructureHandler structureHandler) {

		// Ensure this attribute is only on the <title> element
		if (tag.elementCompleteName != 'title') {
			throw new IllegalArgumentException("${attributeName} processor should only appear in a <title> element")
		}

		def titlePattern = attributeValue
		def modelFactory = context.modelFactory

		def contentTitle = context[CONTENT_TITLE_KEY]
		def layoutTitle = context[LAYOUT_TITLE_KEY]

		// Break the title pattern up into tokens to map to their respective models
		def titleModel = modelFactory.createModel()
		if (layoutTitle && contentTitle) {
			def matcher = TOKEN_PATTERN.matcher(titlePattern)
			while (matcher.find()) {
				def text = titlePattern.substring(matcher.regionStart(), matcher.start())
				if (text) {
					titleModel.add(modelFactory.createText(text))
				}
				def token = matcher.group(1)
				titleModel.addModel(token == TOKEN_LAYOUT_TITLE ? layoutTitle : contentTitle)
				matcher.region(matcher.regionStart() + text.length() + token.length(), titlePattern.length())
			}
			def remainingText = titlePattern.substring(matcher.regionStart())
			if (remainingText) {
				titleModel.add(modelFactory.createText(remainingText))
			}
		}
		else if (contentTitle) {
			titleModel.addModel(contentTitle)
		}
		else if (layoutTitle) {
			titleModel.addModel(layoutTitle)
		}

		structureHandler.setBody(titleModel, true)
	}
}
