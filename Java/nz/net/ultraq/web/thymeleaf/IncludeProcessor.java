
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
 * Processor for the 'layout:include' attribute, does the same as Thymeleaf's
 * include tag but allows for the passing of element fragments to the included
 * page.
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
