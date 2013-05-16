/*
 * Copyright 2013, Emanuel Rabina (http://www.ultraq.net.nz/)
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

package nz.net.ultraq.thymeleaf.tests;

import org.junit.Test;

/**
 * JUnit class containing tests of code that is common across all of the layout
 * dialect processors.
 * 
 * @author Emanuel Rabina
 */
public class CommonFunctionsTester extends AbstractLayoutDialectTester {

	/**
	 * Test the merging of local variables set with the <tt>th:with</tt>
	 * processor, across content and fragment pages.
	 */
	@Test
	public void localVariableMerging() {

		testOK("LocalVariableMerging");
	}

	/**
	 * Test the inheritance of local variables, where those set in the decorator
	 * page using <tt>th:with</tt> are also available in the content pages.
	 */
	@Test
	public void localVariableInheritance() {

		testOK("LocalVariableInheritance");
	}
}
