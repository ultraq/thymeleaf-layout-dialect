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

package nz.net.ultraq.web.thymeleaf;

import static nz.net.ultraq.web.thymeleaf.FragmentProcessor.PROCESSOR_NAME_FRAGMENT_FULL;
import static nz.net.ultraq.web.thymeleaf.FragmentProcessor.FRAGMENT_NAME_PREFIX;
import static nz.net.ultraq.web.thymeleaf.IncludeProcessor.PROCESSOR_NAME_INCLUDE_FULL;

import org.thymeleaf.dom.Element;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Common code for the layout processors that fetch the content of other pages
 * to include into the current template.
 * 
 * @author Emanuel Rabina
 */
public abstract class AbstractContentProcessor extends AbstractProcessor {

	/**
	 * Subclass constructor, set the attribute name that this processor will
	 * respond to.
	 * 
	 * @param attribute
	 */
	protected AbstractContentProcessor(String attribute) {

		super(attribute);
	}

	/**
	 * Find and return all fragments within the given elements without delving
	 * into <tt>layout:include</tt> elements.
	 * 
	 * @param elements
	 * @return Map of prefixed fragment names and their fragment elements.
	 */
	protected Map<String,Object> findFragments(List<Element> elements) {

		HashMap<String,Object> fragments = new HashMap<String,Object>();
		findFragments(fragments, elements);
		return fragments;
	}

	/**
	 * Recursive search for all fragment elements without delving into
	 * <tt>layout:include</tt> elements.
	 * 
	 * @param fragments
	 * @param elements
	 */
	private void findFragments(HashMap<String,Object> fragments, List<Element> elements) {

		for (Element element: elements) {
			String fragmentname = element.getAttributeValue(PROCESSOR_NAME_FRAGMENT_FULL);
			if (fragmentname != null) {
				fragments.put(FRAGMENT_NAME_PREFIX + fragmentname, element.cloneNode(null, true));
			}
			if (!element.hasAttribute(PROCESSOR_NAME_INCLUDE_FULL)) {
				findFragments(fragments, element.getElementChildren());
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int getPrecedence() {

		return 0;
	}
}
