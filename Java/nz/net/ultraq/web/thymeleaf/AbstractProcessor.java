
package nz.net.ultraq.web.thymeleaf;

import org.thymeleaf.processor.attr.AbstractAttrProcessor;

/**
 * Common code for the layout processors.
 * 
 * @author Emanuel Rabina
 */
public abstract class AbstractProcessor extends AbstractAttrProcessor {

	/**
	 * Subclass constructor, set the attribute name that this processor will
	 * respond to.
	 * 
	 * @param attribute
	 */
	protected AbstractProcessor(String attribute) {

		super(attribute);
	}
}
