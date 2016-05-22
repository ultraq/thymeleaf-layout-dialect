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

import org.junit.BeforeClass
import org.junit.Test
import org.thymeleaf.TemplateEngine
import org.thymeleaf.model.IModelFactory
import org.thymeleaf.templatemode.TemplateMode
import static org.junit.Assert.*

/**
 * Tests for the attribute merger, spins up a Thymeleaf template engine so that
 * we can use the model factory for creating models.
 * 
 * @author Emanuel Rabina
 */
class AttributeMergerTests {

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
	 * Test that the merger just adds attributes found in the source to the target
	 * that don't already exist in the target
	 */
	@Test
	void addAttributes() {

		def modelBuilder = new ModelBuilder(modelFactory)

//		<div layout-test:attribute-merger="target" id="test-element"></div>
		def source = modelBuilder.build {
			div(id: 'test-element')
		}

//		<div class="container"></div>
		def target = modelBuilder.build {
			div(class: 'container')
		}

//		<div class="container" id="test-element"></div>
		def expected = modelBuilder.build {
			div(class: 'container', id: 'test-element')
		}

		new AttributeMerger(modelFactory).merge(target, source)

		// TODO: This should be an assertEquals, but somehow it's not using the
		//       metaclass unless I use ==.  Change if I get a chance to add
		//       equals() methods to the model and event classes themselves.
		assertTrue(target == expected)
	}

	/**
	 * Test that attributes in the source element override those of the target.
 	 */
	@Test
	void mergeAttributes() {

		def modelBuilder = new ModelBuilder(modelFactory)

//		<div layout-test:attribute-merger="target" class="roflcopter"></div>
		def source = modelBuilder.build {
			div(class: 'roflcopter')
		}

//		<div class="container"></div>
		def target = modelBuilder.build {
			div(class: 'container')
		}

//		<div class="roflcopter"></div>
		def expected = modelBuilder.build {
			div(class: 'roflcopter')
		}

		new AttributeMerger(modelFactory).merge(target, source)

		// TODO: This should be an assertEquals, but somehow it's not using the
		//       metaclass unless I use ==.  Change if I get a chance to add
		//       equals() methods to the model and event classes themselves.
		assertTrue(target == expected)
	}
}
