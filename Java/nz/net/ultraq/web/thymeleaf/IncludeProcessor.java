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
import static nz.net.ultraq.web.thymeleaf.LayoutDialect.LAYOUT_PREFIX;

import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.dom.Node;
import org.thymeleaf.fragment.FragmentAndTarget;
import org.thymeleaf.processor.ProcessorResult;
import org.thymeleaf.standard.fragment.StandardFragmentProcessor;

import java.util.List;
import java.util.Map;

/**
 * Similar to Thymeleaf's th:include, but allows the passing of entire element
 * fragments to the included template.  Useful if you have some HTML that you
 * want to reuse, but whose contents are too complex to determine or construct
 * with context variables alone.
 * 
 * @author Emanuel Rabina
 */
public class IncludeProcessor extends AbstractContentProcessor {

	static final String PROCESSOR_NAME_INCLUDE = "include";
	static final String PROCESSOR_NAME_INCLUDE_FULL = LAYOUT_PREFIX + ":" + PROCESSOR_NAME_INCLUDE;

	/**
	 * Constructor, sets this processor to work on the 'include' attribute.
	 */
	public IncludeProcessor() {

		super(PROCESSOR_NAME_INCLUDE);
	}

	/**
	 * Locates the specified page and includes it into the current template.
	 * 
	 * @param arguments
	 * @param element
	 * @param attributeName
	 * @return Result of the processing.
	 */
	@Override
	protected ProcessorResult processAttribute(Arguments arguments, Element element, String attributeName) {

		// Locate the page and fragment to include
		FragmentAndTarget fragmentandtarget = StandardFragmentProcessor.computeStandardFragmentSpec(
				arguments.getConfiguration(), arguments, element.getAttributeValue(attributeName),
				null, PROCESSOR_NAME_FRAGMENT_FULL, true);
		List<Node> includefragments = fragmentandtarget.extractFragment(arguments.getConfiguration(),
				arguments.getContext(), arguments.getTemplateRepository());

		element.removeAttribute(attributeName);

		// Gather all fragment parts within the include element
		Map<String,Object> pagefragments = findFragments(element.getElementChildren());

		// Replace the children of this element with those of the include page fragments
		element.clearChildren();
		if (includefragments != null) {
			for (Node includefragment: includefragments) {
				element.addChild(includefragment);
			}
		}

		// Scope any fragments for the inlude page to this element
		if (!pagefragments.isEmpty()) {
			return ProcessorResult.setLocalVariables(pagefragments);
		}
		return ProcessorResult.OK;
	}
}
