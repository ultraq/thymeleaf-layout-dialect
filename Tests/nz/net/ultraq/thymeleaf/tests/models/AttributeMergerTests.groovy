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
import nz.net.ultraq.thymeleaf.models.AttributeMerger
import nz.net.ultraq.thymeleaf.models.ModelBuilder

import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.ITemplateContext
import org.thymeleaf.dialect.IProcessorDialect
import org.thymeleaf.standard.StandardDialect
import org.thymeleaf.templatemode.TemplateMode

/**
 * Tests for the attribute merger, spins up a Thymeleaf template engine so that
 * we can use the model factory for creating models.
 * 
 * @author Emanuel Rabina
 */
class AttributeMergerTests {

	private static ITemplateContext mockContext
	private static ModelBuilder modelBuilder

	private AttributeMerger attributeMerger

	/**
	 * Set up, create a template engine.
	 */
	@BeforeClass
	static void setupThymeleafEngine() {

		def templateEngine = new TemplateEngine(
			additionalDialects: [
				new LayoutDialect()
			]
		)
		def modelFactory = templateEngine.configuration.getModelFactory(TemplateMode.HTML)

		modelBuilder = new ModelBuilder(modelFactory, templateEngine.configuration.elementDefinitions, TemplateMode.HTML)
		mockContext = [
			getConfiguration: { ->
				return templateEngine.configuration
			},
			getModelFactory: { ->
				return modelFactory
			}
		] as ITemplateContext
		mockContext.metaClass {
			getPrefixForDialect = { Class<IProcessorDialect> dialectClass ->
				return dialectClass == StandardDialect ? 'th' :
				       dialectClass == LayoutDialect ? 'layout' :
				       'mock-prefix'
			}
		}
	}

	/**
	 * Set up, create a new attribute merger.
	 */
	@Before
	void setupAttributeMerger() {

		attributeMerger = new AttributeMerger(mockContext)
	}

	/**
	 * Test that the merger just adds attributes found in the source to the target
	 * that don't already exist in the target
	 */
	@Test
	@SuppressWarnings('ExplicitCallToDivMethod')
	void addAttributes() {

		def source = modelBuilder.build {
			div(id: 'test-element')
		}
		def target = modelBuilder.build {
			div(class: 'container')
		}
		def expected = modelBuilder.build {
			div(class: 'container', id: 'test-element')
		}

		def result = attributeMerger.merge(target, source)
		assert result == expected
	}

	/**
	 * Test that the attribute merger doesn't modify the source parameters.
	 */
	@Test
	@SuppressWarnings('ExplicitCallToDivMethod')
	void immutability() {

		def source = modelBuilder.build {
			div(id: 'source-element')
		}
		def target = modelBuilder.build {
			div(id: 'target-element')
		}

		def sourceOrig = source.cloneModel()
		def targetOrig = target.cloneModel()

		attributeMerger.merge(target, source)

		def sourceAfter = source.cloneModel()
		def targetAfter = target.cloneModel()

		assert sourceOrig == sourceAfter
		assert targetOrig == targetAfter
	}

	/**
	 * Test that attributes in the source element override those of the target.
	 */
	@Test
	@SuppressWarnings('ExplicitCallToDivMethod')
	void mergeAttributes() {

		def source = modelBuilder.build {
			div(class: 'roflcopter')
		}
		def target = modelBuilder.build {
			div(class: 'container')
		}
		def expected = modelBuilder.build {
			div(class: 'roflcopter')
		}

		def result = attributeMerger.merge(target, source)
		assert result == expected
	}

	/**
	 * Test attribute merging when {@code th:with} attributes are involved.
	 */
	@Test
	@SuppressWarnings('ExplicitCallToDivMethod')
	void mergeAttributesWith() {

		def source = modelBuilder.build {
			div('th:with': 'value1=\'Hello!\'')
		}
		def target = modelBuilder.build {
			div('th:with': 'value2=\'World!\'')
		}
		def expected = modelBuilder.build {
			div('th:with': 'value1=\'Hello!\',value2=\'World!\'')
		}

		def result = attributeMerger.merge(target, source)
		assert result == expected
	}
}
