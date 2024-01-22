/*
 * Copyright 2024, Emanuel Rabina (http://www.ultraq.net.nz/)
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

package nz.net.ultraq.thymeleaf.layoutdialect.decorators.html

import nz.net.ultraq.thymeleaf.layoutdialect.LayoutDialect
import nz.net.ultraq.thymeleaf.layoutdialect.decorators.strategies.AppendingStrategy
import nz.net.ultraq.thymeleaf.testing.junit.JUnitTestReporter

import org.thymeleaf.standard.StandardDialect
import org.thymeleaf.testing.templateengine.context.web.JavaxServletTestWebExchangeBuilder
import org.thymeleaf.testing.templateengine.context.web.WebProcessingContextBuilder
import org.thymeleaf.testing.templateengine.engine.TestExecutor
import org.thymeleaf.testing.templateengine.report.ConsoleTestReporter
import spock.lang.Specification

/**
 * Test suite for the experimental 'new title tokens' feature from
 * https://github.com/ultraq/thymeleaf-layout-dialect/issues/172
 *
 * Should be removed once I can find a way to not have this enabled via some
 * feature flag 🤔  If/when I do, don't forget to remove the exclusion from the
 * {@code LayoutDialectTestExecutor} file.
 *
 * @author Emanuel Rabina
 */
class TitleTokens extends Specification {

	def "New title tokens"() {
		given:
			def testExecutor = new TestExecutor(
				'TitleTokens',
				new WebProcessingContextBuilder(JavaxServletTestWebExchangeBuilder.create())
			)
			testExecutor.with {
				dialects = [
					new StandardDialect(),
					new LayoutDialect(new AppendingStrategy(), true, true)
				]
				reporter = new JUnitTestReporter(new ConsoleTestReporter())
			}

		when:
			testExecutor.execute('classpath:nz/net/ultraq/thymeleaf/layoutdialect/decorators/html/TitleTokens.thtest')

		then:
			assert testExecutor.reporter.lastResult.ok
	}
}
