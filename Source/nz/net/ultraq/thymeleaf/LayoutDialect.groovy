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

package nz.net.ultraq.thymeleaf

import nz.net.ultraq.thymeleaf.decorators.DecoratorProcessor
import nz.net.ultraq.thymeleaf.decorators.SortingStrategy
import nz.net.ultraq.thymeleaf.decorators.TitlePatternProcessor
import nz.net.ultraq.thymeleaf.decorators.strategies.AppendingStrategy
import nz.net.ultraq.thymeleaf.fragments.FragmentProcessor
import nz.net.ultraq.thymeleaf.includes.IncludeProcessor
import nz.net.ultraq.thymeleaf.includes.ReplaceProcessor
import nz.net.ultraq.thymeleaf.models.ModelExtensions

import org.thymeleaf.dialect.AbstractProcessorDialect
import org.thymeleaf.processor.IProcessor
import org.thymeleaf.standard.processor.StandardXmlNsTagProcessor
import org.thymeleaf.templatemode.TemplateMode

/**
 * Dialect for making use of template/layout decorator pages with Thymeleaf.
 * 
 * @author Emanuel Rabina
 */
class LayoutDialect extends AbstractProcessorDialect {

	private static final String DIALECT_NAME = 'layout'
	private static final String DIALECT_PREFIX = 'layout'
	private static final int DIALECT_PRECEDENCE = 10

	/**
	 * Apply model extensions.
	 */
	static {
		ModelExtensions.apply()
	}

	private final SortingStrategy sortingStrategy

	/**
	 * Constructor, configure the layout dialect with the given values.
	 * 
	 * @param sortingStrategy
	 */
	LayoutDialect(SortingStrategy sortingStrategy = new AppendingStrategy()) {

		super(DIALECT_NAME, DIALECT_PREFIX, DIALECT_PRECEDENCE)
		this.sortingStrategy = sortingStrategy
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	Set<IProcessor> getProcessors(String dialectPrefix) {

		return [
			// Processors available in the HTML template mode
			new StandardXmlNsTagProcessor(TemplateMode.HTML, dialectPrefix),
			new DecoratorProcessor(TemplateMode.HTML, dialectPrefix, sortingStrategy),
			new IncludeProcessor(TemplateMode.HTML, dialectPrefix),
			new ReplaceProcessor(TemplateMode.HTML, dialectPrefix),
			new FragmentProcessor(TemplateMode.HTML, dialectPrefix),
			new TitlePatternProcessor(TemplateMode.HTML, dialectPrefix),

			// Processors available in the XML template mode
			new StandardXmlNsTagProcessor(TemplateMode.XML, dialectPrefix),
			new DecoratorProcessor(TemplateMode.XML, dialectPrefix, sortingStrategy),
			new IncludeProcessor(TemplateMode.XML, dialectPrefix),
			new ReplaceProcessor(TemplateMode.XML, dialectPrefix),
			new FragmentProcessor(TemplateMode.XML, dialectPrefix)
		]
	}
}
