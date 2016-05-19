/* 
 * Copyright 2016, Emanuel Rabina (http://www.ultraq.net.nz/)
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

package nz.net.ultraq.thymeleaf.tests

import nz.net.ultraq.thymeleaf.LayoutDialect
import nz.net.ultraq.thymeleaf.testing.JUnitTestExecutor

import org.junit.runners.Parameterized
import org.thymeleaf.dialect.IDialect
import org.thymeleaf.standard.StandardDialect

/**
 * Temporary test class that executes a handful of Thymeleaf test files so I can
 * make sure that the Thymeleaf 3 migration is making some progress instead of
 * being overwhelmed with test failures everywhere!
 * 
 * @author Emanuel Rabina
 */
class GradualLayoutTestExecutor extends JUnitTestExecutor {

	final List<? extends IDialect> testDialects = [
		new StandardDialect(),
		new LayoutDialect()
	]

	/**
	 * Return just the example Thymeleaf testing file.
	 * 
	 * @return List of just the example test file.
	 */
	@Parameterized.Parameters(name = '{0}')
	static List<String> listGroupingLayoutDialectTests() {

		return [
			'nz/net/ultraq/thymeleaf/tests/decorators/html/Head-ConditionalComments.thtest',
			'nz/net/ultraq/thymeleaf/tests/decorators/strategies/AppendingStrategy.thtest',
			'nz/net/ultraq/thymeleaf/tests/decorators/xml/Document-XmlDecoration.thtest',
			'nz/net/ultraq/thymeleaf/tests/decorators/Decorator-BodyOnlyLayout-BodyOnlyContent.thtest'
		]
	}
}
