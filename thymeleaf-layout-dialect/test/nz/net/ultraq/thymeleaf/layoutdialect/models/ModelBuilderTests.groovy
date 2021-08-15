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
import org.thymeleaf.engine.TemplateData
import org.thymeleaf.engine.TemplateManager
import org.thymeleaf.templatemode.TemplateMode
import spock.lang.Specification

/**
 * Tests the model builder against Thymeleaf's normal model-creation mechanisms
 * to make sure they're equivalent.
 * 
 * @author Emanuel Rabina
 */
class ModelBuilderTests extends Specification {

	private ModelBuilder modelBuilder
	private TemplateManager templateManager

	/**
	 * Set up, create a template engine.
	 */
	def setup() {

		def templateEngine = new TemplateEngine(
			additionalDialects: [
				new LayoutDialect()
			]
		)
		templateManager = templateEngine.configuration.templateManager
		modelBuilder = new ModelBuilder(templateEngine.configuration.getModelFactory(TemplateMode.HTML),
			templateEngine.configuration.elementDefinitions, TemplateMode.HTML)
	}

	def "Compare with Thymeleaf's model builder"() {
		given:
			def modelFromTemplate = templateManager.parseString(
				new TemplateData('test', null, null, TemplateMode.HTML, null),
				'''
					<html>
					<head>
						<title>Model comparison</title>
						<meta charset="utf-8">
						<meta name="description" value="bad void tag"></meta>
					</head>
					<body>
						<main>
							<header>
								<h1>Hello!</h1>
							</header>
							<hr/>
							<div class="content">
								<p>Some random text</p>
							</div>
						</main>
					</body>
					</html>
				'''.stripIndent().trim(), 0, 0, TemplateMode.HTML, false)
				.cloneModel() // To drop the template start/end events

		when:
			def modelFromBuilder = modelBuilder.build {
				html {
					head {
						title('Model comparison')
						meta(charset: 'utf-8', void: true)
						meta(name: 'description', value: 'bad void tag')
					}
					body {
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
				}
			}

		then:
			def nonWhitespaceEvents = { event -> !event.whitespace }
			assert modelFromBuilder.findAll(nonWhitespaceEvents) == modelFromTemplate.findAll(nonWhitespaceEvents)
	}
}
