/*
 * Copyright 2017, Emanuel Rabina (http://www.ultraq.net.nz/)
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
import org.thymeleaf.engine.Text
import org.thymeleaf.model.IProcessableElementTag
import org.thymeleaf.processor.element.AbstractAttributeTagProcessor
import org.thymeleaf.processor.element.IElementTagStructureHandler
import org.thymeleaf.templatemode.TemplateMode

/**
 * Processor produced from FragmentProcessor in order to separate include and
 * define logic to avoid ambiguity.
 *
 * @authors Emanuel Rabina, George Vinokhodov
 */
class CollectFragmentProcessor extends AbstractAttributeTagProcessor {

	private static final Logger logger = LoggerFactory.getLogger(CollectFragmentProcessor)

	private static boolean warned = false

	static final String PROCESSOR_DEFINE = 'define'
	static final String PROCESSOR_COLLECT = 'collect'
	static final int PROCESSOR_PRECEDENCE = 1

	/**
	 * Constructor, sets this processor to work on the 'collect' attribute.
	 * 
	 * @param templateMode
	 * @param dialectPrefix
	 */
	CollectFragmentProcessor(TemplateMode templateMode, String dialectPrefix) {

		super(templateMode, dialectPrefix, null, false, PROCESSOR_COLLECT, true, PROCESSOR_PRECEDENCE, true)
	}

	/**
	 * Inserts the content of <code>:define</code> fragments into the encountered collect placeholder.
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

		// All :define fragments we collected, :collect fragments included to determine where to stop.
		// Fragments after :collect are preserved for the next :collect event
		def fragments = FragmentMap.get(context)[(attributeValue)]

		// Replace the tag body with the fragment
		if (fragments) {
			def modelFactory = context.modelFactory
			def merger = new ElementMerger(context)
			def replacementModel = modelFactory.createModel(tag)
			def first = true
			while (!fragments.empty) {
				def fragment = fragments.poll()
				if (fragment.get(0).getAttributeValue(dialectPrefix, CollectFragmentProcessor.PROCESSOR_COLLECT)) {
					break
				}
				if (first) {
					replacementModel = merger.merge(replacementModel, fragment)
					first = false
				}
				else {
					def firstEvent = true
					fragment.each {
						event ->
						if (firstEvent) {
							firstEvent = false
							replacementModel.add(new Text('\n'))
							replacementModel.add(modelFactory.removeAttribute(event, dialectPrefix, PROCESSOR_DEFINE))
						}
						else {
							replacementModel.add(event)
						}
					}
				}
			}

			// Remove the layout:collect attribute - Thymeleaf won't do it for us
			// when using StructureHandler.replaceWith(...)
			replacementModel.replace(0, modelFactory.removeAttribute(replacementModel.first(), dialectPrefix, PROCESSOR_COLLECT))

			structureHandler.replaceWith(replacementModel, true)
		}
	}
}
