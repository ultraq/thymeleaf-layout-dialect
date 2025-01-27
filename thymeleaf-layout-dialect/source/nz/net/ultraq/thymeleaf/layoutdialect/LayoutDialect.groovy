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

package nz.net.ultraq.thymeleaf.layoutdialect

import nz.net.ultraq.thymeleaf.layoutdialect.decorators.DecorateProcessor
import nz.net.ultraq.thymeleaf.layoutdialect.decorators.SortingStrategy
import nz.net.ultraq.thymeleaf.layoutdialect.decorators.TitlePatternProcessor
import nz.net.ultraq.thymeleaf.layoutdialect.decorators.TitleProcessor
import nz.net.ultraq.thymeleaf.layoutdialect.decorators.strategies.AppendingStrategy
import nz.net.ultraq.thymeleaf.layoutdialect.fragments.CollectFragmentProcessor
import nz.net.ultraq.thymeleaf.layoutdialect.fragments.FragmentProcessor
import nz.net.ultraq.thymeleaf.layoutdialect.includes.IncludeProcessor
import nz.net.ultraq.thymeleaf.layoutdialect.includes.InsertProcessor
import nz.net.ultraq.thymeleaf.layoutdialect.includes.ReplaceProcessor

import org.thymeleaf.dialect.AbstractProcessorDialect
import org.thymeleaf.processor.IProcessor
import org.thymeleaf.standard.processor.StandardXmlNsTagProcessor
import org.thymeleaf.templatemode.TemplateMode

/**
 * A dialect for Thymeleaf that lets you build layouts and reusable templates in
 * order to improve code reuse.
 * <p>
 * To configure the layout dialect, you have 2 options:
 * <ul>
 *   <li>the constructor with arguments {@code LayoutDialect(SortingStrategy, boolean)}</li>
 *   <li>the fluent API methods {@link #withAutoHeadMerging}, {@link #withExperimentalTitleTokens},
 *    and {@link #withSortingStrategy}</li>
 * </ul>
 * <p>Note that the fluent API is currently the only way to enable the {@code experimentatlTitleTokens}
 * setting.  With the number of options growing, the constructor might be
 * deprecated in favour of the fluent API methods.
 *
 * @author Emanuel Rabina
 */
class LayoutDialect extends AbstractProcessorDialect {

	static final String DIALECT_NAME = 'Layout'
	static final String DIALECT_PREFIX = 'layout'
	static final int DIALECT_PRECEDENCE = 10

	private SortingStrategy sortingStrategy = new AppendingStrategy()

	/**
	 * Experimental option, set to {@code false} to skip the automatic merging
	 * of an HTML {@code <head>} section.
	 */
	private boolean autoHeadMerging = true

	/**
	 * Experimental option, set to {@code true} to use standard Thymeleaf
	 * expression syntax for title patterns and to have access to the title parts
	 * in templates as the variables {@code layoutDialectContentTitle} and
	 * {@code layoutDialectLayoutTitle}.
	 */
	private boolean experimentalTitleTokens = false

	/**
	 * Constructor, configure the layout dialect.
	 *
	 * @param sortingStrategy
	 * @param autoHeadMerging
	 *   Experimental option, set to {@code false} to skip the automatic merging
	 *   of an HTML {@code <head>} section.
	 */
	LayoutDialect(SortingStrategy sortingStrategy = new AppendingStrategy(), boolean autoHeadMerging = true) {

		super(DIALECT_NAME, DIALECT_PREFIX, DIALECT_PRECEDENCE)

		this.sortingStrategy = sortingStrategy
		this.autoHeadMerging = autoHeadMerging
	}

	/**
	 * Returns the layout dialect's processors.
	 *
	 * @param dialectPrefix
	 * @return All of the processors for HTML and XML template modes.
	 */
	@Override
	Set<IProcessor> getProcessors(String dialectPrefix) {

		return [
			// Processors available in the HTML template mode
			new StandardXmlNsTagProcessor(TemplateMode.HTML, dialectPrefix),
			new DecorateProcessor(TemplateMode.HTML, dialectPrefix, sortingStrategy, autoHeadMerging, experimentalTitleTokens),
			new IncludeProcessor(TemplateMode.HTML, dialectPrefix),
			new InsertProcessor(TemplateMode.HTML, dialectPrefix),
			new ReplaceProcessor(TemplateMode.HTML, dialectPrefix),
			new FragmentProcessor(TemplateMode.HTML, dialectPrefix),
			new CollectFragmentProcessor(TemplateMode.HTML, dialectPrefix),
			new TitlePatternProcessor(TemplateMode.HTML, dialectPrefix),
			new TitleProcessor(TemplateMode.HTML, dialectPrefix),

			// Processors available in the XML template mode
			new StandardXmlNsTagProcessor(TemplateMode.XML, dialectPrefix),
			new DecorateProcessor(TemplateMode.XML, dialectPrefix, sortingStrategy, autoHeadMerging, experimentalTitleTokens),
			new IncludeProcessor(TemplateMode.XML, dialectPrefix),
			new InsertProcessor(TemplateMode.XML, dialectPrefix),
			new ReplaceProcessor(TemplateMode.XML, dialectPrefix),
			new FragmentProcessor(TemplateMode.XML, dialectPrefix),
			new CollectFragmentProcessor(TemplateMode.XML, dialectPrefix)
		]
	}

	/**
	 * Configure this instance of the layout dialect with the given {@code autoHeadMerging}
	 * value.
	 */
	LayoutDialect withAutoHeadMerging(boolean autoHeadMerging) {

		this.autoHeadMerging = autoHeadMerging
		return this
	}

	/**
	 * Configure this instance of the layout dialect with the given {@code experimentalTitleTokens}
	 * value.
	 */
	LayoutDialect withExperimentalTitleTokens(boolean experimentalTitleTokens) {

		this.experimentalTitleTokens = experimentalTitleTokens
		return this
	}

	/**
	 * Configure this instance of the layout dialect with the given {@code sortingStrategy}.
	 */
	LayoutDialect withSortingStrategy(SortingStrategy sortingStrategy) {

		this.sortingStrategy = sortingStrategy
		return this
	}
}
