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

package nz.net.ultraq.thymeleaf.tests.models.extensions

import nz.net.ultraq.thymeleaf.LayoutDialect
import nz.net.ultraq.thymeleaf.models.ModelBuilder
import nz.net.ultraq.thymeleaf.models.extensions.ChildModelIterator

import org.junit.BeforeClass
import org.junit.Test
import org.thymeleaf.TemplateEngine
import org.thymeleaf.templatemode.TemplateMode

/**
 * Tests for the {@link ChildModelIterator} class.
 * 
 * @author Emanuel Rabina
 */
class ChildModelIteratorTests {

	private static ModelBuilder modelBuilder

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
		modelBuilder = new ModelBuilder(templateEngine.configuration.getModelFactory(TemplateMode.HTML),
			templateEngine.configuration.elementDefinitions, TemplateMode.HTML)
	}

	/**
	 * Test the child model iterator to retrieve only the immediate children of a
	 * model as their own models.
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
		assert nextModel.equalsIgnoreWhitespace(pModel1)
		assert nextModel.startIndex == 1
		assert nextModel.endIndex == 4

		nextModel = childModelIterator.next()
		assert nextModel.equalsIgnoreWhitespace(hrModel)
		assert nextModel.startIndex == 4
		assert nextModel.endIndex == 5

		nextModel = childModelIterator.next()
		assert nextModel.equalsIgnoreWhitespace(pModel2)
		assert nextModel.startIndex == 5
		assert nextModel.endIndex == 8

		assert childModelIterator.hasNext() == false
	}
}
