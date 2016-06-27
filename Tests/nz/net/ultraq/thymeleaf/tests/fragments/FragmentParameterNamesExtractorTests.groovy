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

package nz.net.ultraq.thymeleaf.tests.fragments

import nz.net.ultraq.thymeleaf.fragments.FragmentParameterNamesExtractor

import org.junit.Before
import org.junit.Test
import static org.junit.Assert.*

/**
 * Tests for the fragment parameter names extractor.
 * 
 * @author Emanuel Rabina
 */
class FragmentParameterNamesExtractorTests {

	private FragmentParameterNamesExtractor fragmentParameterNamesExtractor

	@Before
	void setup() {

		fragmentParameterNamesExtractor = new FragmentParameterNamesExtractor()
	}

	/**
	 * Test that a fragment definition without any parameters returns an empty
	 * list.
	 */
	@Test
	void noParameters() {

		def fragmentDefinition = 'simple-definition'
		def parameterNames = fragmentParameterNamesExtractor.extract(fragmentDefinition)
		assertEquals([], parameterNames)
	}

	/**
	 * Test that a fragment definition with parameters has all names extracted.
	 */
	@Test
	void hasParameters() {

		def fragmentDefinition = 'complex-definition(param1=${expression},param2=\'something\')'
		def parameterNames = fragmentParameterNamesExtractor.extract(fragmentDefinition)
		assertEquals(['param1', 'param2'], parameterNames)
	}

	/**
	 * Test a fragment definition without a full assignation sequence.
	 */
	@Test
	void hasParametersNoAssignment() {

		def fragmentDefinition = 'no-assignment(param)'
		def parameterNames = fragmentParameterNamesExtractor.extract(fragmentDefinition)
		assertEquals(['param'], parameterNames)
	}

	/**
	 * Test some whitespace cases.
	 */
	@Test
	void hasParametersWhitespace() {

		def fragmentDefinition = 'complex-definition ( param1 = ${expression}, param2 )'
		def parameterNames = fragmentParameterNamesExtractor.extract(fragmentDefinition)
		assertEquals(['param1', 'param2'], parameterNames)
	}
}
