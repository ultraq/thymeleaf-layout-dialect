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

package nz.net.ultraq.thymeleaf.tests.include;

import nz.net.ultraq.thymeleaf.tests.AbstractLayoutDialectTester;

import org.junit.Test;

/**
 * JUnit class containing tests related to the <tt>layout:substituteby</tt>
 * attribute processor.
 * 
 * @author Emanuel Rabina
 */
public class ReplaceTester extends AbstractLayoutDialectTester {

	/**
	 * Test that fragment elements get copied over to the included page.
	 */
	@Test
	public void elementInclusion() {

		testOK("include/ElementInclusion");
	}

	/**
	 * Duplicate test for the deprecated 'substituteby' processor.
	 */
	@Test
	public void elementInclusionSubstituteBy() {

		testOK("include/ElementInclusionSubstituteBy");
	}
}
