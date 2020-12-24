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

package nz.net.ultraq.thymeleaf.fragments

import nz.net.ultraq.thymeleaf.models.ElementMerger

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.thymeleaf.context.ITemplateContext
import org.thymeleaf.engine.AttributeName
import org.thymeleaf.model.IProcessableElementTag
import org.thymeleaf.processor.element.AbstractAttributeTagProcessor
import org.thymeleaf.processor.element.IElementTagStructureHandler
import org.thymeleaf.templatemode.TemplateMode

/**
 * This processor serves a dual purpose: to mark sections of the template that
 * can be replaced, and to do the replacing when they're encountered.
 * 
 * @author Emanuel Rabina
 */
class FragmentProcessor extends AbstractAttributeTagProcessor {

	private static final Logger logger = LoggerFactory.getLogger(FragmentProcessor)

	private static boolean warned = false

	static final String PROCESSOR_NAME = 'fragment'
	static final int PROCESSOR_PRECEDENCE = 1

	/**
	 * Constructor, sets this processor to work on the 'fragment' attribute.
	 * 
	 * @param templateMode
	 * @param dialectPrefix
	 */
	FragmentProcessor(TemplateMode templateMode, String dialectPrefix) {

		super(templateMode, dialectPrefix, null, false, PROCESSOR_NAME, true, PROCESSOR_PRECEDENCE, true)
	}

	/**
	 * Inserts the content of fragments into the encountered fragment placeholder.
	 * 
	 * @param context
	 * @param model
	 * @param attributeName
	 * @param attributeValue
	 * @param structureHandler
	 */
	@Override
	@SuppressWarnings('AssignmentToStaticFieldFromInstanceMethod')
	protected void doProcess(ITemplateContext context, IProcessableElementTag tag,
		AttributeName attributeName, String attributeValue, IElementTagStructureHandler structureHandler) {

		// Emit a warning if found in the <head> section
		if (templateMode == TemplateMode.HTML &&
		    context.elementStack.any { element -> element.elementCompleteName == 'head' }) {
			if (!warned) {
				logger.warn(
					'You don\'t need to put the layout:fragment/data-layout-fragment attribute into the <head> section - ' +
					'the decoration process will automatically copy the <head> section of your content templates into your layout page.'
				)
				warned = true
			}
		}

		// Locate the fragment that corresponds to this decorator/include fragment
		def fragments = FragmentMap.get(context)[(attributeValue)]

		// Replace the tag body with the fragment
		if (fragments) {
			def fragment = fragments.last()
			def modelFactory = context.modelFactory
			def replacementModel = new ElementMerger(context).merge(modelFactory.createModel(tag), fragment)

			// Remove the layout:fragment attribute - Thymeleaf won't do it for us
			// when using StructureHandler.replaceWith(...)
			replacementModel.replace(0, modelFactory.removeAttribute(replacementModel.first(),
				dialectPrefix, PROCESSOR_NAME))

			structureHandler.replaceWith(replacementModel, true)
		}
	}
}
