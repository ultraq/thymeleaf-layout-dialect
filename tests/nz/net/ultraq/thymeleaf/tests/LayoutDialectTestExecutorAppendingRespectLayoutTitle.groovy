/*
 * Copyright 2019, Emanuel Rabina (http://www.ultraq.net.nz/)
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
import nz.net.ultraq.thymeleaf.decorators.strategies.AppendingRespectLayoutTitleStrategy
import nz.net.ultraq.thymeleaf.testing.JUnitTestExecutor

import org.junit.runners.Parameterized.Parameters
import org.reflections.Reflections
import org.reflections.scanners.ResourcesScanner
import org.thymeleaf.dialect.IDialect
import org.thymeleaf.standard.StandardDialect

/**
 * A parameterized JUnit test class that is run over just the files involved in
 * testing the {@link AppendingRespectLayoutTitleStrategy} head element sorter.
 * 
 * @author Emanuel Rabina
 */
class LayoutDialectTestExecutorAppendingRespectLayoutTitle extends JUnitTestExecutor {

	final List<? extends IDialect> testDialects = [
		new StandardDialect(),
		new LayoutDialect(new AppendingRespectLayoutTitleStrategy())
	]

	/**
	 * Return only Thymeleaf testing files involved in the testing of the
	 * {@link AppendingRespectLayoutTitleStrategy} head element sorter.
	 * 
	 * @return List of all the Thymeleaf testing files for the grouping head
	 *         element sorter.
	 */
	@Parameters(name = '{0}')
	static List<String> listGroupingLayoutDialectTests() {

		return new Reflections('', new ResourcesScanner())
			.getResources(~/AppendingRespectLayoutTitleStrategy.*\.thtest/) as List
	}
}
