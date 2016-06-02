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
import org.thymeleaf.engine.TemplateData
import org.thymeleaf.templatemode.TemplateMode
import static org.junit.Assert.*

/**
 * Tests the model builder against Thymeleaf's normal model-creation mechanisms
 * to make sure they're equivalent.
 * 
 * @author Emanuel Rabina
 */
class ModelBuilderTests {

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
	 * Compares a model created by the builder with a similar model created by
	 * Thymeleaf's template engine.
	 */
	@Test
	void compareModel() {

		def templateManager = templateEngine.configuration.templateManager
		def modelFromTemplate = templateManager.parseString(
			new TemplateData('test', null, null, TemplateMode.HTML, null),
			'''
				<main>
					<header>
						<h1>Hello!</h1>
					</header>
					<hr/>
					<div class="content">
						<p>Some random text</p>
					</div>
				</main>
			'''.stripIndent().trim(), 0, 0, TemplateMode.HTML, false)
			.cloneModel() // To drop the template start/end events

		def modelFromBuilder = modelBuilder.build {
			main {
				header {
					h1('Hello!')
				}
				hr(standalone: true)
				div(class: 'content') {
					p('Some random text')
				}
			}
		}

		assertTrue(modelFromTemplate.equalsIgnoreWhitespace(modelFromBuilder))
	}
}
