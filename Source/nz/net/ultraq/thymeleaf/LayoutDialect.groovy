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

import nz.net.ultraq.thymeleaf.context.extensions.IContextExtensions
import nz.net.ultraq.thymeleaf.decorators.DecorateProcessor
import nz.net.ultraq.thymeleaf.decorators.DecoratorProcessor
import nz.net.ultraq.thymeleaf.decorators.SortingStrategy
import nz.net.ultraq.thymeleaf.decorators.TitlePatternProcessor
import nz.net.ultraq.thymeleaf.decorators.strategies.AppendingStrategy
import nz.net.ultraq.thymeleaf.fragments.FragmentProcessor
import nz.net.ultraq.thymeleaf.includes.IncludeProcessor
import nz.net.ultraq.thymeleaf.includes.InsertProcessor
import nz.net.ultraq.thymeleaf.includes.ReplaceProcessor
import nz.net.ultraq.thymeleaf.models.extensions.IAttributeExtensions
import nz.net.ultraq.thymeleaf.models.extensions.ICloseElementTagExtensions
import nz.net.ultraq.thymeleaf.models.extensions.IModelExtensions
import nz.net.ultraq.thymeleaf.models.extensions.IOpenElementTagExtensions
import nz.net.ultraq.thymeleaf.models.extensions.IStandaloneElementTagExtensions
import nz.net.ultraq.thymeleaf.models.extensions.ITemplateEventExtensions
import nz.net.ultraq.thymeleaf.models.extensions.ITextExtensions
import nz.net.ultraq.thymeleaf.models.extensions.TemplateModelExtensions

import org.thymeleaf.dialect.AbstractProcessorDialect
import org.thymeleaf.processor.IProcessor
import org.thymeleaf.standard.processor.StandardXmlNsTagProcessor
import org.thymeleaf.templatemode.TemplateMode

/**
 * A dialect for Thymeleaf that lets you build layouts and reusable templates in
 * order to improve code reuse
 * 
 * @author Emanuel Rabina
 */
class LayoutDialect extends AbstractProcessorDialect {

	static final String DIALECT_NAME = 'Layout'
	static final String DIALECT_PREFIX = 'layout'
	static final int DIALECT_PRECEDENCE = 10

	/**
	 * Apply object extensions.
	 */
	static {
		[
			// Context extensions
			IContextExtensions,

			// Model extensions
			IAttributeExtensions,
			ICloseElementTagExtensions,
			IModelExtensions,
			IOpenElementTagExtensions,
			IStandaloneElementTagExtensions,
			ITemplateEventExtensions,
			ITextExtensions,
			TemplateModelExtensions

		]*.apply()
	}

	private final SortingStrategy sortingStrategy

	/**
	 * Constructor, configure the layout dialect with the given sorting strategy.
	 * 
	 * @param sortingStrategy
	 */
	LayoutDialect(SortingStrategy sortingStrategy = new AppendingStrategy()) {

		super(DIALECT_NAME, DIALECT_PREFIX, DIALECT_PRECEDENCE)
		this.sortingStrategy = sortingStrategy
	}

	/**
	 * Returns the layout dialect's processors.
	 * 
	 * @param dialectPrefix
	 * @return All of the processors for HTML and XML template modes.
	 */
	@Override
	Set<IProcessor> getProcessors(String dialectPrefix) {

		// TODO: Many of the underlying classes don't respect the runtime-configured
		//       dialect prefix, so I'll need to do something about that later.
		//       https://github.com/ultraq/thymeleaf-layout-dialect/issues/103

		return [
			// Processors available in the HTML template mode
			new StandardXmlNsTagProcessor(TemplateMode.HTML, dialectPrefix),
			new DecorateProcessor(TemplateMode.HTML, dialectPrefix, sortingStrategy),
			new DecoratorProcessor(TemplateMode.HTML, dialectPrefix, sortingStrategy),
			new IncludeProcessor(TemplateMode.HTML, dialectPrefix),
			new InsertProcessor(TemplateMode.HTML, dialectPrefix),
			new ReplaceProcessor(TemplateMode.HTML, dialectPrefix),
			new FragmentProcessor(TemplateMode.HTML, dialectPrefix),
			new TitlePatternProcessor(TemplateMode.HTML, dialectPrefix),

			// Processors available in the XML template mode
			new StandardXmlNsTagProcessor(TemplateMode.XML, dialectPrefix),
			new DecorateProcessor(TemplateMode.XML, dialectPrefix, sortingStrategy),
			new DecoratorProcessor(TemplateMode.XML, dialectPrefix, sortingStrategy),
			new IncludeProcessor(TemplateMode.XML, dialectPrefix),
			new InsertProcessor(TemplateMode.XML, dialectPrefix),
			new ReplaceProcessor(TemplateMode.XML, dialectPrefix),
			new FragmentProcessor(TemplateMode.XML, dialectPrefix)
		]
	}
}
