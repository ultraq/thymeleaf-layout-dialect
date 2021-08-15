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

package nz.net.ultraq.thymeleaf.layoutdialect.fragments

import nz.net.ultraq.thymeleaf.layoutdialect.LayoutDialect
import nz.net.ultraq.thymeleaf.layoutdialect.models.ModelBuilder

import org.thymeleaf.TemplateEngine
import org.thymeleaf.templatemode.TemplateMode
import spock.lang.Specification

/**
 * Tests for the {@link FragmentFinder} utility.
 * 
 * @author Emanuel Rabina
 */
class FragmentFinderTests extends Specification {

	private ModelBuilder modelBuilder
	private FragmentFinder fragmentFinder

	def setup() {

		def templateEngine = new TemplateEngine(
			additionalDialects: [
				new LayoutDialect()
			]
		)
		modelBuilder = new ModelBuilder(templateEngine.configuration.getModelFactory(TemplateMode.HTML),
			templateEngine.configuration.elementDefinitions, TemplateMode.HTML)

		fragmentFinder = new FragmentFinder('layout')
	}

	@SuppressWarnings('ExplicitCallToDivMethod')
	def "Finds basic fragments and returns a map of names:fragments"() {
		given:
			def source = modelBuilder.build {
				main {
					header('layout:fragment': 'header-fragment')
					div {
						p('layout:fragment': 'paragraph-fragment')
					}
					footer('layout:fragment': 'footer-fragment')
				}
			}

		when:
			def fragments = fragmentFinder.findFragments(source)

		then:
			fragments.containsKey('header-fragment')
			fragments.containsKey('paragraph-fragment')
			fragments.containsKey('footer-fragment')
	}
}
