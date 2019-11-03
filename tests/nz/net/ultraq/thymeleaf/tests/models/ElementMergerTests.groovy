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
import nz.net.ultraq.thymeleaf.models.ElementMerger
import nz.net.ultraq.thymeleaf.models.ModelBuilder

import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.ITemplateContext
import org.thymeleaf.dialect.IProcessorDialect
import org.thymeleaf.templatemode.TemplateMode
import spock.lang.Specification

/**
 * Tests for the element merger.
 * 
 * @author Emanuel Rabina
 */
class ElementMergerTests extends Specification {

	private ITemplateContext mockContext
	private ModelBuilder modelBuilder
	private ElementMerger elementMerger

	/**
	 * Set up, create a template engine and element merger.
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
				return 'mock-prefix'
			}
		}

		elementMerger = new ElementMerger(mockContext)
	}

	def "Doesn't modify source parameters"() {
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
			elementMerger.merge(target, source)

		then:
			sourceOrig == source.cloneModel()
			targetOrig == target.cloneModel()
	}

	def "Merge source elements into the target"() {
		given:
			def source = modelBuilder.build {
				section {
					header()
				}
			}
			def target = modelBuilder.build {
				div {
					p('Hello')
				}
			}

		when:
			def result = elementMerger.merge(target, source)

		then:
			result == source
	}

	def "Merge source root element attributes into the target root element"() {
		given:
			def source = modelBuilder.build {
				div(id: 'source-id')
			}
			def target = modelBuilder.build {
				div(id: 'target-id')
			}

		when:
			def result = elementMerger.merge(target, source)

		then:
			result == source
	}

	def "Merging root element attributes doesn't mess with the root element type"() {
		given:
			def source = modelBuilder.build {
				section {
					header()
				}
			}
			def target = modelBuilder.build {
				div(standalone: true)
			}

		when:
			def result = elementMerger.merge(target, source)

		then:
			result == source
	}
}
