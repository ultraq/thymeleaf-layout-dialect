/* 
 * Copyright 2019, Emanuel Rabina (http://www.ultraq.net.nz/)
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

package nz.net.ultraq.thymeleaf.tests.decorators.strategies

import nz.net.ultraq.thymeleaf.LayoutDialect
import nz.net.ultraq.thymeleaf.decorators.SortingStrategy
import nz.net.ultraq.thymeleaf.decorators.strategies.GroupingStrategy
import nz.net.ultraq.thymeleaf.models.ModelBuilder

import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.thymeleaf.TemplateEngine
import org.thymeleaf.templatemode.TemplateMode

/**
 * Test the results of the grouping strategy.
 * 
 * @author Emanuel Rabina
 */
class GroupingStrategyTest {

	private static ModelBuilder modelBuilder

	private SortingStrategy groupingStrategy

	/**
	 * Set up, create a template engine for the model builder.
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
	}

	/**
	 * Set up, create a new grouping strategy.
	 */
	@Before
	void setupGroupingStrategy() {

		groupingStrategy = new GroupingStrategy()
	}

	/**
	 * Whitespace nodes return a -1 index value to mean to discard them.
	 */
	@Test
	void DiscardWhitespace() {

		def headModel = modelBuilder.build {
			head()
		}
		def whitespace = modelBuilder.build {
			p('   ')
		}.getModel(1)
		def result = groupingStrategy.findPositionForModel(headModel, whitespace)
		assert result == -1
	}

	/**
	 * Historic behaviour, have <title> elements always be first.
	 */
	@Test
	void TitleFirst() {

		def titleModel = modelBuilder.build {
			title('Page title')
		}

		def headModel = modelBuilder.build {
			head()
		}
		def result = groupingStrategy.findPositionForModel(headModel, titleModel)
		assert result == 1

		headModel = modelBuilder.build {
			head('   ')
		}
		result = groupingStrategy.findPositionForModel(headModel, titleModel)
		assert result == 2

		headModel = modelBuilder.build {
			head  {
				meta(charset: 'UTF-8')
			}
		}
		result = groupingStrategy.findPositionForModel(headModel, titleModel)
		assert result == 1
	}
}
