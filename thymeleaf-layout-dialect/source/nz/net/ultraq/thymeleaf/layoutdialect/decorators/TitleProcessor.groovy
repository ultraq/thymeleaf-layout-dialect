/*
 * Copyright 2024, Emanuel Rabina (http://www.ultraq.net.nz/)
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

import org.thymeleaf.context.ITemplateContext
import org.thymeleaf.engine.AttributeName
import org.thymeleaf.model.IProcessableElementTag
import org.thymeleaf.processor.element.AbstractAttributeTagProcessor
import org.thymeleaf.processor.element.IElementTagStructureHandler
import org.thymeleaf.templatemode.TemplateMode

/**
 * A processor for the experimental feature of using standard Thymeleaf
 * expression syntax when creating the {@code <title>} element, as opposed to
 * the special syntax required of {@code layout:title-pattern}.
 *
 * @author Emanuel Rabina
 */
class TitleProcessor extends AbstractAttributeTagProcessor {

	static final String PROCESSOR_NAME = 'title'
	static final int PROCESSOR_PRECEDENCE = 1

	static final String CONTENT_TITLE_KEY = 'layoutDialectContentTitle'
	static final String LAYOUT_TITLE_KEY = 'layoutDialectLayoutTitle'

	/**
	 * Constructor, sets this processor to work on the 'title' attribute.
	 */
	TitleProcessor(TemplateMode templateMode, String dialectPrefix) {

		super(templateMode, dialectPrefix, null, false, PROCESSOR_NAME, true, PROCESSOR_PRECEDENCE, true)
	}

	/**
	 * Process the {@code layout:title} directive, replaces the title text using
	 * values extracted from the layout and content templates and emitted with
	 * standard Thymeleaf expression syntax.
	 */
	@Override
	protected void doProcess(ITemplateContext context, IProcessableElementTag tag, AttributeName attributeName,
		String attributeValue, IElementTagStructureHandler structureHandler) {

		// Ensure this attribute is only on the <title> element
		if (tag.elementCompleteName != 'title') {
			throw new IllegalArgumentException("${attributeName} processor should only appear in a <title> element")
		}

		structureHandler.setBody(new ExpressionProcessor(context).processAsString(attributeValue), false)
	}
}
