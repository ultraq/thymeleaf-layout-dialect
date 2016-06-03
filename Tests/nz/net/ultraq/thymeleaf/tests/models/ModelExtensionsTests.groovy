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
import nz.net.ultraq.thymeleaf.models.ModelBuilder

import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.thymeleaf.TemplateEngine
import org.thymeleaf.templatemode.TemplateMode
import static org.junit.Assert.*

/**
 * Tests for some of the more complicated additions to the model class.
 * 
 * @author Emanuel Rabina
 */
class ModelExtensionsTests {

	private static TemplateEngine templateEngine
	private ModelBuilder modelBuilder

	/**
	 * Set up, create a template engine.
	 */
	@BeforeClass
	static void setupThymeleafEngine() {

		templateEngine = new TemplateEngine(
			additionalDialects: [
				new LayoutDialect()
			]
		)
	}

	/**
	 * Set up, create a model builder.
	 */
	@Before
	void setupModelBuilder() {

		modelBuilder = new ModelBuilder(templateEngine.configuration.getModelFactory(TemplateMode.HTML))
	}

	/**
	 * Test the child model iterator.
	 */
	@Test
	void childModelIterator() {

		def pModel1 = modelBuilder.build {
			p('Test paragraph')
		}
		def hrModel = modelBuilder.build {
			hr(standalone: true)
		}
		def pModel2 = modelBuilder.build {
			p('Another test paragraph')
		}

		def model = modelBuilder.build {
			div(class: 'content') {
				add(pModel1)
				add(hrModel)
				add(pModel2)
			}
		}

		def childModelIterator = model.childModelIterator()

		def nextModel = childModelIterator.next()
		assertTrue(nextModel.equalsIgnoreWhitespace(pModel1))

		nextModel = childModelIterator.next()
		assertTrue(hrModel.equalsIgnoreWhitespace(nextModel))

		nextModel = childModelIterator.next()
		assertTrue(pModel2.equalsIgnoreWhitespace(nextModel))

		assertFalse(childModelIterator.hasNext())
	}

	/**
	 * Test the retrieval of models for various element types.
	 */
	@Test
	void getModel() {

		def headerModel = modelBuilder.build {
			header {
				h1('Test title')
			}
		}
		def hrModel = modelBuilder.build {
			hr(standalone: true)
		}
		def divModel = modelBuilder.build {
			div(class: 'content') {
				p('Test paragraph')
				add(hrModel)
				p('Another test paragraph')
			}
		}

		def model = modelBuilder.build {
			section {
				add(headerModel)
				add(divModel)
			}
		}

		def headerModelExtract = model.getModel(1)
		assertTrue(headerModel.equalsIgnoreWhitespace(headerModelExtract))

		def divModelExtract = model.getModel(6)
		assertTrue(divModel.equalsIgnoreWhitespace(divModelExtract))

		def hrModelExtract = model.getModel(10)
		assertTrue(hrModel.equalsIgnoreWhitespace(hrModelExtract))
	}
}
