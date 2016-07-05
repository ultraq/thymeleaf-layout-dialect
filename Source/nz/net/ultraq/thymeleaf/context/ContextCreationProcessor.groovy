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

package nz.net.ultraq.thymeleaf.context

import org.thymeleaf.context.ITemplateContext
import org.thymeleaf.model.ITemplateEnd
import org.thymeleaf.model.ITemplateStart
import org.thymeleaf.processor.templateboundaries.AbstractTemplateBoundariesProcessor
import org.thymeleaf.processor.templateboundaries.ITemplateBoundariesStructureHandler
import org.thymeleaf.templatemode.TemplateMode

/**
 * A processor whose only job is to initialize the layout context.
 * 
 * @author Emanuel Rabina
 */
class ContextCreationProcessor extends AbstractTemplateBoundariesProcessor {

	static final int PROCESSOR_PRECEDENCE = 10

	/**
	 * Constructor, sets the template mode of the processor.
	 * 
	 * @param templateMode
	 */
	ContextCreationProcessor(TemplateMode templateMode) {

		super(templateMode, PROCESSOR_PRECEDENCE)
	}

	/**
	 * Creates and sets the layout context on the template context so it's
	 * available everywhere else on the template.
	 * 
	 * @param context
	 * @param templateStart
	 * @param structureHandler
	 */
	@Override
	void doProcessTemplateStart(ITemplateContext context, ITemplateStart templateStart,
		ITemplateBoundariesStructureHandler structureHandler) {

		context[LayoutContext.CONTEXT_KEY] = new LayoutContext()
	}

	/**
	 * Does nothing.
	 * 
	 * @param context
	 * @param templateEnd
	 * @param structureHandler
	 */
	@Override
	void doProcessTemplateEnd(ITemplateContext context, ITemplateEnd templateEnd,
		ITemplateBoundariesStructureHandler structureHandler) {
	}
}
