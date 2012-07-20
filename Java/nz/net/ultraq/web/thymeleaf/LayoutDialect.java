
package nz.net.ultraq.web.thymeleaf;

import org.thymeleaf.dialect.AbstractDialect;
import org.thymeleaf.processor.IProcessor;

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

		HashSet<IProcessor> processors = new HashSet<IProcessor>();
		processors.add(new DecoratorProcessor());
		processors.add(new IncludeProcessor());
		processors.add(new FragmentProcessor());
		return processors;
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
}
