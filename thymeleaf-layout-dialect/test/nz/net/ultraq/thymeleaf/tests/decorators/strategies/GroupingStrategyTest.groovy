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

package nz.net.ultraq.thymeleaf.tests.decorators.strategies

import nz.net.ultraq.thymeleaf.LayoutDialect
import nz.net.ultraq.thymeleaf.decorators.strategies.GroupingStrategy
import nz.net.ultraq.thymeleaf.models.ModelBuilder

import org.thymeleaf.TemplateEngine
import org.thymeleaf.templatemode.TemplateMode
import spock.lang.Specification

/**
 * Test the results of the grouping strategy.
 * 
 * @author Emanuel Rabina
 */
@SuppressWarnings(['AssignmentToStaticFieldFromInstanceMethod', 'PrivateFieldCouldBeFinal'])
class GroupingStrategyTest extends Specification {

	private static ModelBuilder modelBuilder
	private GroupingStrategy groupingStrategy = new GroupingStrategy()

	/**
	 * Set up a model builder.  Has to be done as a setupSpec otherwise it can't
	 * be used in a where: block.
	 */
	def setupSpec() {
		def templateEngine = new TemplateEngine(
			additionalDialects: [
				new LayoutDialect()
			]
		)
		def modelFactory = templateEngine.configuration.getModelFactory(TemplateMode.HTML)

		modelBuilder = new ModelBuilder(modelFactory, templateEngine.configuration.elementDefinitions, TemplateMode.HTML)
	}

	def "Whitespace nodes return a -1 index value to mean to discard them"() {
		given:
			def headModel = modelBuilder.build {
				head()
			}
			def whitespace = modelBuilder.build {
				p('   ')
			}.getModel(1)

		when:
			def result = groupingStrategy.findPositionForModel(headModel, whitespace)

		then:
			result == -1
	}
}
