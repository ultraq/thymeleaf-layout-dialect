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

package nz.net.ultraq.thymeleaf.tests.models

import nz.net.ultraq.thymeleaf.LayoutDialect
import nz.net.ultraq.thymeleaf.models.VariableDeclarationMerger

import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.ExpressionContext
import spock.lang.Specification

/**
 * Tests for the variable declaration merger.
 * 
 * @author Emanuel Rabina
 */
@SuppressWarnings('GStringExpressionWithinString')
class VariableDeclarationMergerTests extends Specification {

	private VariableDeclarationMerger merger

	/**
	 * Set up, create an expression context.
	 */
	def setup() {

		def templateEngine = new TemplateEngine(
			additionalDialects: [
				new LayoutDialect()
			]
		)
		def context = new ExpressionContext(templateEngine.configuration)

		merger = new VariableDeclarationMerger(context)
	}

	def "Concatenates values when none of the names in the source and target match"() {
		given:
			def target = 'name1=${value1}'
			def source = 'name2=${value2}'

		when:
			def result = merger.merge(target, source)

		then:
			result == 'name1=${value1},name2=${value2}'
	}

	def "Replaces target values when the source value has the same name"() {
		given:
			def target = 'name=${value1}'
			def source = 'name=${value2}'

		when:
			def result = merger.merge(target, source)

		then:
			result == 'name=${value2}'
	}

	def "Replaces only the target value with the same name in the source"() {
		given:
			def target = 'name1=${value1},name2=${value2}'
			def source = 'name1=${newValue},name3=${value3}'

		when:
			def result = merger.merge(target, source)

		then:
			result == 'name1=${newValue},name2=${value2},name3=${value3}'
	}

	def "Uses the target value when no source present"() {
		given:
			def target = 'name=${value}'
			def source = null

		when:
			def result = merger.merge(target, source)

		then:
			result == target
	}

	def "Uses the source value when no target is present"() {
		given:
			def target = null
			def source = 'name=${value}'

		when:
			def result = merger.merge(target, source)

		then:
			result == source
	}
}
