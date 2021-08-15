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

package nz.net.ultraq.thymeleaf.layoutdialect.models

import nz.net.ultraq.thymeleaf.layoutdialect.LayoutDialect

import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.ITemplateContext
import org.thymeleaf.dialect.IProcessorDialect
import org.thymeleaf.standard.StandardDialect
import org.thymeleaf.templatemode.TemplateMode
import spock.lang.Specification

/**
 * Tests for the attribute merger, spins up a Thymeleaf template engine so that
 * we can use the model factory for creating models.
 * 
 * @author Emanuel Rabina
 */
@SuppressWarnings('ExplicitCallToDivMethod')
class AttributeMergerTests extends Specification {

	private ITemplateContext mockContext
	private ModelBuilder modelBuilder
	private AttributeMerger attributeMerger

	/**
	 * Set up, create a template engine.
	 */
	def setup() {

		def templateEngine = new TemplateEngine(
			additionalDialects: [
				new LayoutDialect()
			]
		)
		def modelFactory = templateEngine.configuration.getModelFactory(TemplateMode.HTML)

		modelBuilder = new ModelBuilder(modelFactory, templateEngine.configuration.elementDefinitions, TemplateMode.HTML)

		mockContext = Mock(ITemplateContext)
		mockContext.configuration >> templateEngine.configuration
		mockContext.modelFactory >> modelFactory
		mockContext.metaClass {
			getPrefixForDialect = { Class<IProcessorDialect> dialectClass ->
				return dialectClass == StandardDialect ? 'th' :
				       dialectClass == LayoutDialect ? 'layout' :
				       'mock-prefix'
			}
		}

		attributeMerger = new AttributeMerger(mockContext)
	}

	def "Adds attributes from the source that don't already exist in the target"() {
		given:
			def source = modelBuilder.build {
				div(id: 'test-element')
			}
			def target = modelBuilder.build {
				div(class: 'container')
			}
			def expected = modelBuilder.build {
				div(class: 'container', id: 'test-element')
			}

		when:
			def result = attributeMerger.merge(target, source)

		then:
			assert result == expected
	}

	def "Doesn't modify the source parameters"() {
		given:
			def source = modelBuilder.build {
				div(id: 'source-element')
			}
			def target = modelBuilder.build {
				div(id: 'target-element')
			}
			def sourceOrig = source.cloneModel()
			def targetOrig = target.cloneModel()

		when:
			attributeMerger.merge(target, source)

		then:
			sourceOrig == source.cloneModel()
			targetOrig == target.cloneModel()
	}

	def "Override target attributes"() {
		given:
			def source = modelBuilder.build {
				div(class: 'roflcopter')
			}
			def target = modelBuilder.build {
				div(class: 'container')
			}

		when:
			def result = attributeMerger.merge(target, source)

		then:
			result == modelBuilder.build {
				div(class: 'roflcopter')
			}
	}

	def "Merge th:with attributes"() {
		given:
			def source = modelBuilder.build {
				div('th:with': "value1='Hello!'")
			}
			def target = modelBuilder.build {
				div('th:with': "value2='World!'")
			}

		when:
			def result = attributeMerger.merge(target, source)

		then:
			result == modelBuilder.build {
				div('th:with': "value1='Hello!',value2='World!'")
			}
	}
}
