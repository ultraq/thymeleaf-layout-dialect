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

import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import org.thymeleaf.standard.StandardDialect
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver
import spock.lang.Specification

/**
 * A test of the layout dialect but changing the standard and layout dialect
 * prefixes to ensure that they are respected.
 * 
 * @author Emanuel Rabina
 */
class ConfigurablePrefixTest extends Specification {

	// Template engine with altered dialect prefixes
	def templateEngine = new TemplateEngine(
		dialectsByPrefix: [
			t: new StandardDialect(),
			l: new LayoutDialect()
		],
		templateResolver: new ClassLoaderTemplateResolver(
			prefix: 'nz/net/ultraq/thymeleaf/tests/ConfigurablePrefixTest-',
			suffix: '.html'
		)
	)

	def "Example layout with changed prefixes"() {
		given:
			def processingResultAsTokens = { template ->
				def result = templateEngine.process(template, new Context())
				return result.split(/(\t|\n)/).findAll { token -> token.size() > 0 }
			}

		when:
			def actualAsTokens = processingResultAsTokens('Content')
			def expectedAsTokens = processingResultAsTokens('Result')

		then:
			actualAsTokens == expectedAsTokens
	}
}
