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

import static nz.net.ultraq.thymeleaf.DecoratorUtilities.*;
import static nz.net.ultraq.thymeleaf.FragmentProcessor.FRAGMENT_NAME_PREFIX;
import static nz.net.ultraq.thymeleaf.FragmentProcessor.PROCESSOR_NAME_FRAGMENT;
import static nz.net.ultraq.thymeleaf.LayoutDialect.DIALECT_PREFIX_LAYOUT;
import static nz.net.ultraq.thymeleaf.include.IncludeProcessor.PROCESSOR_NAME_INCLUDE;

import org.thymeleaf.dom.Element;
import org.thymeleaf.processor.attr.AbstractAttrProcessor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Common code for processors that fetch the content of other pages to include
 * into the current template.
 * 
 * @author Emanuel Rabina
 */
public abstract class AbstractContentProcessor extends AbstractAttrProcessor {

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
	protected static Map<String,Object> findFragments(List<Element> elements) {

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
	private static void findFragments(HashMap<String,Object> fragments, List<Element> elements) {

		for (Element element: elements) {
			String fragmentname = getAttributeValue(element, DIALECT_PREFIX_LAYOUT, PROCESSOR_NAME_FRAGMENT);
			if (fragmentname != null) {
				fragments.put(FRAGMENT_NAME_PREFIX + fragmentname, element.cloneNode(null, true));
			}
			if (!hasAttribute(element, DIALECT_PREFIX_LAYOUT, PROCESSOR_NAME_INCLUDE)) {
				findFragments(fragments, element.getElementChildren());
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getPrecedence() {

		return 0;
	}
}
