
package nz.net.ultraq.web.thymeleaf;

import static nz.net.ultraq.web.thymeleaf.DecoratorProcessor.PROCESSOR_NAME_DECORATOR;
import static nz.net.ultraq.web.thymeleaf.FragmentProcessor.PROCESSOR_NAME_FRAGMENT;
import static nz.net.ultraq.web.thymeleaf.IncludeProcessor.PROCESSOR_NAME_INCLUDE;

import org.thymeleaf.dialect.AbstractDialect;
import org.thymeleaf.processor.IProcessor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Dialect for making use of template/layout decorator pages with Thymeleaf.
 * 
 * @author Emanuel Rabina
 */
public class LayoutDialect extends AbstractDialect {

	public static final String LAYOUT_NAMESPACE = "http://www.ultraq.net.nz/web/thymeleaf/layout";

	static final String LAYOUT_PREFIX = "layout";

	private final HashMap<String,IProcessor> processors = new HashMap<String,IProcessor>();

	/**
	 * Default constructor, sets up the dialect to not use title extension.
	 * This is the same as calling {@link #LayoutDialect(String)} with the
	 * argument of "none".
	 */
	public LayoutDialect() {

		this("none");
	}

	/**
	 * Constructor, sets up the dialect with a specific title extension mode.
	 * 
	 * @param extendtitle Specifies on which side of the layout's title to
	 * 					  append the content's title.  One of "left", "right",
	 * 					  or "none".  If the value is neither "left" nor
	 * 					  "right", then the mode will be set to none.
	 */
	public LayoutDialect(String extendtitle) {

		processors.put(PROCESSOR_NAME_DECORATOR, new DecoratorProcessor());
		processors.put(PROCESSOR_NAME_FRAGMENT,  new FragmentProcessor());
		processors.put(PROCESSOR_NAME_INCLUDE,   new IncludeProcessor());

		setExtendTitle(extendtitle);
	}

	/**
	 * Return the layout prefix.
	 * 
	 * @return <tt>layout</tt>
	 */
	@Override
	public String getPrefix() {

		return LAYOUT_PREFIX;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<IProcessor> getProcessors() {

		return new HashSet<IProcessor>(processors.values());
	}

	/**
	 * This dialect is not lenient.
	 * 
	 * @return <tt>false</tt>
	 */
	@Override
	public boolean isLenient() {

		return false;
	}

	/**
	 * Set the title extension mode for the dialect.
	 * 
	 * @param extendtitle Specifies on which side of the layout's title to
	 * 					  append the content's title.  One of "left", "right",
	 * 					  or "none".  If the value is neither "left" nor
	 * 					  "right", then the mode will be set to none.
	 */
	public void setExtendTitle(String extendtitle) {

		((DecoratorProcessor)processors.get(PROCESSOR_NAME_DECORATOR)).setExtendTitle(extendtitle);
	}
}
