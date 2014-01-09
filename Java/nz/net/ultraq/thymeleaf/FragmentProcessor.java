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

package nz.net.ultraq.thymeleaf;

import static nz.net.ultraq.thymeleaf.LayoutUtilities.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.processor.ProcessorResult;
import org.thymeleaf.processor.attr.AbstractAttrProcessor;

/**
 * Marks sections of the template that can be replaced by sections in the
 * content template (if decorating) or passed along to included pages (if
 * including), which share the same name.
 * 
 * @author Emanuel Rabina
 */
public class FragmentProcessor extends AbstractAttrProcessor {

	private static final Logger logger = LoggerFactory.getLogger(FragmentProcessor.class);

	public static final String PROCESSOR_NAME_FRAGMENT = "fragment";

	static final String FRAGMENT_NAME_PREFIX = "fragment-name::";

	/**
	 * Constructor, sets this processor to work on the 'fragment' attribute.
	 */
	public FragmentProcessor() {

		super(PROCESSOR_NAME_FRAGMENT);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int getPrecedence() {

		return 1;
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
		if (element.getOriginalName().equals(HTML_ELEMENT_TITLE)) {
			logger.warn("You don't need to put the layout:fragment attribute into the <title> element - " +
					"the decoration process will automatically override the <title> with the one in " +
					"your content page, if present.");
		}

		// Locate the page fragment that corresponds to this decorator/include fragment
		String fragmentname = element.getAttributeValue(attributeName);
		Element pagefragment = (Element)arguments.getLocalVariable(FRAGMENT_NAME_PREFIX + fragmentname);
		element.removeAttribute(attributeName);

		// Replace the decorator/include fragment with the page fragment
		if (pagefragment != null) {
			pullAttributes(pagefragment, element);
			pullContent(element, pagefragment);
		}

		return ProcessorResult.OK;
	}
}
