/*
 * Copyright 2013, Emanuel Rabina (http://www.ultraq.net.nz/)
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

package nz.net.ultraq.thymeleaf.include;

import nz.net.ultraq.thymeleaf.AbstractContentProcessor;
import static nz.net.ultraq.thymeleaf.FragmentProcessor.PROCESSOR_NAME_FRAGMENT;
import static nz.net.ultraq.thymeleaf.LayoutDialect.DIALECT_PREFIX_LAYOUT;

import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.dom.Node;
import org.thymeleaf.processor.ProcessorResult;
import org.thymeleaf.standard.fragment.StandardFragment;
import org.thymeleaf.standard.fragment.StandardFragmentProcessor;

import java.util.List;
import java.util.Map;

/**
 * Similar to Thymeleaf's <tt>th:substituteby</tt>, but allows the passing of
 * entire element fragments to the included template.  Useful if you have some
 * HTML that you want to reuse, but whose contents are too complex to determine
 * or construct with context variables alone.
 * 
 * @deprecated Use the {@link ReplaceProcessor} instead.
 * @author Emanuel Rabina
 */
@Deprecated
public class SubstituteByProcessor extends AbstractContentProcessor {

	public static final String PROCESSOR_NAME_SUBSTITUTEBY = "substituteby";
	public static final String PROCESSOR_NAME_SUBSTITUTEBY_FULL = DIALECT_PREFIX_LAYOUT + ":" + PROCESSOR_NAME_SUBSTITUTEBY;

	/**
	 * Constructor, set this processor to work on the 'substituteby' attribute.
	 */
	public SubstituteByProcessor() {

		super(PROCESSOR_NAME_SUBSTITUTEBY);
	}

	/**
	 * Locates the specified page/fragment and brings it into the current
	 * template.
	 * 
	 * @param arguments
	 * @param element
	 * @param attributeName
	 * @return Result of the processing.
	 */
	@Override
	protected ProcessorResult processAttribute(Arguments arguments, Element element, String attributeName) {

		// Locate the page and fragment to include
		StandardFragment fragment = StandardFragmentProcessor.computeStandardFragmentSpec(
				arguments.getConfiguration(), arguments, element.getAttributeValue(attributeName),
				DIALECT_PREFIX_LAYOUT, PROCESSOR_NAME_FRAGMENT);
		List<Node> includefragments = fragment.extractFragment(arguments.getConfiguration(),
				arguments, arguments.getTemplateRepository());

		element.removeAttribute(attributeName);

		// Gather all fragment parts within the substituteby element
		Map<String,Object> pagefragments = findFragments(element.getElementChildren());

		// Replace the children of this element with those of the substituteby
		// page fragments, scoping any fragment parts to the immediate children
		element.clearChildren();
		if (includefragments != null) {
			for (Node includefragment: includefragments) {
				includefragment.setAllNodeLocalVariables(pagefragments);
				element.addChild(includefragment);
			}
		}
		element.getParent().extractChild(element);

		return ProcessorResult.OK;
	}
}
