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

package nz.net.ultraq.thymeleaf.layoutdialect.fragments

import spock.lang.Specification

/**
 * Tests for the fragment parameter names extractor.
 * 
 * @author Emanuel Rabina
 */
@SuppressWarnings(['GStringExpressionWithinString', 'PrivateFieldCouldBeFinal'])
class FragmentParameterNamesExtractorTests extends Specification {

	private FragmentParameterNamesExtractor fragmentParameterNamesExtractor = new FragmentParameterNamesExtractor()

	def "A fragment definition without parameters returns an empty list"() {
		given:
			def fragmentDefinition = 'simple-definition'
		when:
			def parameterNames = fragmentParameterNamesExtractor.extract(fragmentDefinition)
		then:
			parameterNames == []
	}

	def "A fragment definition with parameters has all names extracted"() {
		given:
			def fragmentDefinition = 'complex-definition(param1=${expression},param2=\'something\')'
		when:
			def parameterNames = fragmentParameterNamesExtractor.extract(fragmentDefinition)
		then:
			parameterNames == ['param1', 'param2']
	}

	def "A fragment definition without a full assignation sequence is extracted"() {
		given:
			def fragmentDefinition = 'no-assignment(param)'
		when:
			def parameterNames = fragmentParameterNamesExtractor.extract(fragmentDefinition)
		then:
			parameterNames == ['param']
	}

	def "Some whitespace cases"() {
		given:
			def fragmentDefinition = 'complex-definition ( param1 = ${expression}, param2 )'
		when:
			def parameterNames = fragmentParameterNamesExtractor.extract(fragmentDefinition)
		then:
			parameterNames == ['param1', 'param2']
	}
}
