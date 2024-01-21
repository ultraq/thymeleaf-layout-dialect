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

package nz.net.ultraq.thymeleaf.layoutdialect.decorators.html

import nz.net.ultraq.thymeleaf.layoutdialect.LayoutDialect
import nz.net.ultraq.thymeleaf.layoutdialect.decorators.strategies.AppendingStrategy
import nz.net.ultraq.thymeleaf.layoutdialect.models.ModelBuilder

import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.ITemplateContext
import org.thymeleaf.dialect.IProcessorDialect
import org.thymeleaf.standard.StandardDialect
import org.thymeleaf.templatemode.TemplateMode
import spock.lang.Specification

/**
 * Unit tests for the HTML head decorator.
 *
 * @author Emanuel Rabina
 */
class HtmlHeadDecoratorTests extends Specification {

	private ITemplateContext mockContext
	private ModelBuilder modelBuilder

	private HtmlHeadDecorator htmlHeadDecorator

	/**
	 * Set up, create a template engine, HTML head decorator.
	 */
	def setup() {

		def templateEngine = new TemplateEngine(
			additionalDialects: [
				new LayoutDialect()
			]
		)
		def modelFactory = templateEngine.configuration.getModelFactory(TemplateMode.HTML)

		modelBuilder = new ModelBuilder(modelFactory, templateEngine.configuration.elementDefinitions, TemplateMode.HTML)
		mockContext = Mock(ITemplateContext) {
			getConfiguration() >> templateEngine.configuration
			getModelFactory() >> modelFactory
			getTemplateMode() >> TemplateMode.HTML
		}
		mockContext.metaClass {
			getPrefixForDialect = { Class<IProcessorDialect> dialectClass ->
				return dialectClass == StandardDialect ? 'th' :
				       dialectClass == LayoutDialect ? 'layout' :
				       'mock-prefix'
			}
		}

		htmlHeadDecorator = new HtmlHeadDecorator(mockContext, new AppendingStrategy())
	}

	def "Doesn't modify source parameters"() {
		given:
			def content = modelBuilder.build {
				head {
					title('Content page')
					script(src: 'content-script.js')
				}
			}
			def layout = modelBuilder.build {
				head {
					title('Layout page')
					script(src: 'common-script.js')
				}
			}
			def contentOrig = content.cloneModel()
			def layoutOrig = layout.cloneModel()

		when:
			htmlHeadDecorator.decorate(layout, content)

		then:
			contentOrig == content.cloneModel()
			layoutOrig == layout.cloneModel()
	}
}
