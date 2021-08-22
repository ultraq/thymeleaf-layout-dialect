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

package nz.net.ultraq.thymeleaf.layoutdialect

import nz.net.ultraq.thymeleaf.testing.junit.JUnitTestExecutor

import org.reflections.Reflections
import org.reflections.scanners.ResourcesScanner
import org.thymeleaf.dialect.IDialect
import org.thymeleaf.standard.StandardDialect

/**
 * A test executor for just the example Thymeleaf test files in this package.
 * 
 * @author Emanuel Rabina
 */
class LayoutDialectExampleExecutor extends JUnitTestExecutor {

	final List<? extends IDialect> testDialects = [
		new StandardDialect(),
		new LayoutDialect()
	]

	/**
	 * Return only the example Thymeleaf test files.
	 * 
	 * @return List of the example Thymeleaf test files.
	 */
	static List<String> getThymeleafTestFiles() {

		return new Reflections('', new ResourcesScanner())
			.getResources(~/Examples.*\.thtest/) as List
	}
}
