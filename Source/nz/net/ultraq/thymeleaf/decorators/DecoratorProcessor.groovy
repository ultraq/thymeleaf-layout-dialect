/* 
 * Copyright 2016, Emanuel Rabina (http://www.ultraq.net.nz/)
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
import org.thymeleaf.context.ITemplateContext
import org.thymeleaf.engine.AttributeName
import org.thymeleaf.model.IModel
import org.thymeleaf.processor.element.IElementModelStructureHandler
import org.thymeleaf.templatemode.TemplateMode

/**
 * An alias of the {@link DecorateProcessor} to warn people that the
 * {@code layout:decorator}/{@code data-layout-decorator} processor has been
 * renamed.
 * 
 * @author Emanuel Rabina
 */
@Deprecated
class DecoratorProcessor extends DecorateProcessor {

	private static final Logger logger = LoggerFactory.getLogger(DecoratorProcessor)

	private static boolean warned = false

	static final String PROCESSOR_NAME = 'decorator'

	/**
	 * Constructor, configure this processor to work on the old 'decorator'
	 * attribute and to use the given sorting strategy.
	 * 
	 * @param templateMode
	 * @param dialectPrefix
	 * @param sortingStrategy
	 */
	DecoratorProcessor(TemplateMode templateMode, String dialectPrefix, SortingStrategy sortingStrategy) {

		super(templateMode, dialectPrefix, sortingStrategy, PROCESSOR_NAME)
	}

	/**
	 * Logs a deprecation warning before delegating to the decorate processor.
	 * 
	 * @param context
	 * @param model
	 * @param attributeName
	 * @param attributeValue
	 * @param structureHandler
	 */
	@Override
	@SuppressWarnings('AssignmentToStaticFieldFromInstanceMethod')
	protected void doProcess(ITemplateContext context, IModel model, AttributeName attributeName,
		String attributeValue, IElementModelStructureHandler structureHandler) {

		if (!warned) {
			logger.warn(
				'The layout:decorator/data-layout-decorator processor has been deprecated and will be removed in the next major version of the layout dialect.  ' +
				'Please use layout:decorate/data-layout-decorate instead to future-proof your code.  ' +
				'See https://github.com/ultraq/thymeleaf-layout-dialect/issues/95 for more information.'
			)
			warned = true
		}

		super.doProcess(context, model, attributeName, attributeValue, structureHandler)
	}
}
