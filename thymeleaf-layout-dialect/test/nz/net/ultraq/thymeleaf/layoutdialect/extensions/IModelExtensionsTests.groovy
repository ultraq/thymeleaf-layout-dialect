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

package nz.net.ultraq.thymeleaf.layoutdialect.extensions

import nz.net.ultraq.thymeleaf.layoutdialect.LayoutDialect
import nz.net.ultraq.thymeleaf.layoutdialect.models.ModelBuilder

import org.thymeleaf.TemplateEngine
import org.thymeleaf.templatemode.TemplateMode
import spock.lang.Specification

/**
 * Tests for some of the more complicated additions to the model class.
 *
 * @author Emanuel Rabina
 */
@SuppressWarnings('ExplicitCallToDivMethod')
class IModelExtensionsTests extends Specification {

	private ModelBuilder modelBuilder

	/**
	 * Set up, create a template engine.
	 */
	def setup() {

		def templateEngine = new TemplateEngine(
			additionalDialects: [
				new LayoutDialect()
			]
		)
		modelBuilder = new ModelBuilder(templateEngine.configuration.getModelFactory(TemplateMode.HTML),
			templateEngine.configuration.elementDefinitions, TemplateMode.HTML)
	}

	def "Retrieve models with standard HTML/XML elements"() {
		given:
			def model = modelBuilder.build {
				section {
					header {
						h1('Test title')
					}
					div(class: 'content') {
						p('Test paragraph')
						p('Another test paragraph')
					}
				}
			}

		when:
			def modelExtract = model.getModel(0)

		then:
			modelExtract == model
	}

	def "Retrieve models with standalone elements"() {
		given:
			def model = modelBuilder.build {
				div {
					hr(standalone: true)
				}
			}

		when:
			def modelExtract = model.getModel(0)

		then:
			modelExtract == model
	}

	/**
	 * Tests the retrieval of void elements that are neither self-closed or have
	 * a matching closing tag, as per the HTML spec.
	 */
	def "Retrieve models with void elements"() {
		given:
			def model = modelBuilder.build {
				head {
					meta(charset: 'utf-8', void: true)
				}
			}

		when:
			def modelExtract = model.getModel(0)

		then:
			modelExtract == model
	}

	/**
	 * Tests the retrieval of void elements that have a closing tag, which, isn't
	 * correct HTML as per the spec, but as devs transition from XML-based
	 * Thymeleaf 2 to HTML-based Thymeleaf 3, we may see a lot of as seen here:
	 * https://github.com/ultraq/thymeleaf-layout-dialect/issues/110
	 */
	def "Retrieve models with closed void elements"() {
		given:
			def model = modelBuilder.build {
				head {
					meta(charset: 'utf-8')
				}
			}

		when:
			def modelExtract = model.getModel(0)

		then:
			modelExtract == model
	}
}
