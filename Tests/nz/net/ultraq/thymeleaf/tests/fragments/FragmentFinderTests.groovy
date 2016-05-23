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

package nz.net.ultraq.thymeleaf.tests.fragments

import nz.net.ultraq.thymeleaf.LayoutDialect
import nz.net.ultraq.thymeleaf.fragments.FragmentFinder
import nz.net.ultraq.thymeleaf.models.ModelBuilder

import org.junit.BeforeClass
import org.junit.Test
import org.thymeleaf.TemplateEngine
import org.thymeleaf.model.IModelFactory
import org.thymeleaf.templatemode.TemplateMode
import static org.junit.Assert.*

/**
 * Tests for the {@link FragmentFinder} utility.
 * 
 * @author Emanuel Rabina
 */
class FragmentFinderTests {

	private static IModelFactory modelFactory

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
		modelFactory = templateEngine.configuration.getModelFactory(TemplateMode.HTML)
	}

	/**
	 * Test that the fragment finder finds basic fragments and returns a map whose
	 * keys are the names of the found fragments.
	 */
	@Test
	void findFragments() {

		def modelBuilder = new ModelBuilder(modelFactory)
		def source = modelBuilder.build {
			main {
				header('layout:fragment': 'header-fragment')
				div {
					p('layout:fragment': 'paragraph-fragment')
				}
				footer('layout:fragment': 'footer-fragment')
			}
		}

		def fragmentFinder = new FragmentFinder('layout')
		def fragments = fragmentFinder.findFragments(source)

		assertTrue(fragments.containsKey('header-fragment'))
		assertTrue(fragments.containsKey('paragraph-fragment'))
		assertTrue(fragments.containsKey('footer-fragment'))
	}
}
