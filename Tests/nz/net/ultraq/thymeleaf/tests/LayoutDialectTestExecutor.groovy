/*
 * Copyright 2014, Emanuel Rabina (http://www.ultraq.net.nz/)
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
import nz.net.ultraq.thymeleaf.decorators.strategies.AppendingStrategy
import nz.net.ultraq.thymeleaf.testing.JUnitTestExecutor

import org.junit.runners.Parameterized.Parameters
import org.reflections.Reflections
import org.reflections.scanners.ResourcesScanner
import org.thymeleaf.dialect.IDialect
import org.thymeleaf.standard.StandardDialect

/**
 * A parameterized JUnit test class that is run over every Thymeleaf testing
 * file (.thtest) in the test directory with the standard
 * {@link AppendingStrategy} head element sorter.
 * 
 * @author Emanuel Rabina
 */
class LayoutDialectTestExecutor extends JUnitTestExecutor {

	final List<? extends IDialect> testDialects = [
		new StandardDialect(),
		new LayoutDialect()
	]

	/**
	 * Return all Thymeleaf test files that use the standard
	 * {@link AppendingStrategy} head element sorter.
	 * 
	 * @return List of all Thymeleaf test files for the standard head element
	 *         sorter.
	 */
	@Parameters(name = '{0}')
	static List<String> listStandardLayoutDialectTests() {

		def tests = new Reflections('', new ResourcesScanner())
			.getResources(~/(?!GroupingStrategy|Examples).*\.thtest/) as List
		def exclusions = [
		  'nz/net/ultraq/thymeleaf/tests/decorators/Decorate-DeepHierarchy.thtest',
			'nz/net/ultraq/thymeleaf/tests/decorators/html/TitlePattern-AllowOtherProcessors.thtest',
			'nz/net/ultraq/thymeleaf/tests/decorators/html/TitlePattern-DynamicContent.thtest',
			'nz/net/ultraq/thymeleaf/tests/decorators/html/TitlePattern-ResultAccessible.thtest'
		]
		return tests - exclusions
	}
}
