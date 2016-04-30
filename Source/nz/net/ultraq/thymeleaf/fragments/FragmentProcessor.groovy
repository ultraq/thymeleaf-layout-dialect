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

import nz.net.ultraq.thymeleaf.fragments.mergers.ElementMerger

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.thymeleaf.Arguments
import org.thymeleaf.dom.Element
import org.thymeleaf.processor.ProcessorResult
import org.thymeleaf.processor.attr.AbstractAttrProcessor

/**
 * Marks sections of the template that can be replaced by sections in the
 * content template (if decorating) or passed along to included pages (if
 * including), which share the same name.
 * 
 * @author Emanuel Rabina
 */
class FragmentProcessor extends AbstractAttrProcessor {

	private static final Logger logger = LoggerFactory.getLogger(FragmentProcessor)

	static final String PROCESSOR_NAME = 'fragment'

	final int precedence = 1

	/**
	 * Constructor, sets this processor to work on the 'fragment' attribute.
	 */
	FragmentProcessor() {

		super(PROCESSOR_NAME_FRAGMENT)
	}

	/**
	 * Includes or replaces the content of fragments into the corresponding
	 * fragment placeholder.
	 * 
	 * @param arguments
	 * @param element
	 * @param attributeName
	 * @return Processing result
	 */
	@Override
	protected ProcessorResult processAttribute(Arguments arguments, Element element, String attributeName) {

		// Emit a warning if found in the <title> element
		if (element.originalName == 'title') {
			logger.warn("""
				You don't need to put the layout:fragment attribute into the <title> element -
				the decoration process will automatically override the <title> with the one in
				your content page, if present.
			""".stripIndent())
		}

		// Locate the page fragment that corresponds to this decorator/include fragment
		def fragmentName = element.getAttributeValue(attributeName)
		def fragment = FragmentMap.get(arguments)[(fragmentName)]
		element.removeAttribute(attributeName)

		// Replace the decorator/include fragment with the page fragment
		if (fragment != null) {
			new ElementMerger().merge(element, fragment)
		}

		return ProcessorResult.OK
	}
}
