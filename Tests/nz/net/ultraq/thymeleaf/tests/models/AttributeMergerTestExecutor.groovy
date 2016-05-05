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

import nz.net.ultraq.thymeleaf.models.AttributeMerger
import nz.net.ultraq.thymeleaf.models.ModelExtensions
import nz.net.ultraq.thymeleaf.models.ModelFinder
import nz.net.ultraq.thymeleaf.testing.JUnitTestExecutor

import org.junit.runners.Parameterized.Parameters
import org.reflections.Reflections
import org.reflections.scanners.ResourcesScanner
import org.thymeleaf.context.ITemplateContext
import org.thymeleaf.dialect.AbstractProcessorDialect
import org.thymeleaf.dialect.IDialect
import org.thymeleaf.engine.AttributeName
import org.thymeleaf.model.IProcessableElementTag
import org.thymeleaf.processor.IProcessor
import org.thymeleaf.processor.element.AbstractAttributeTagProcessor
import org.thymeleaf.processor.element.IElementTagStructureHandler
import org.thymeleaf.standard.StandardDialect
import org.thymeleaf.templatemode.TemplateMode

/**
 * Thymeleaf test executor for just the attribute merger tests.  Creates a basic
 * dialect and processor whose only job is to do attribute merging.
 * 
 * @author Emanuel Rabina
 */
class AttributeMergerTestExecutor extends JUnitTestExecutor {

	final List<IDialect> testDialects = [
	  new StandardDialect(),
		new AttributeMergerTestDialect()
	]

	/**
	 * Return only the attribute merger Thymeleaf test files.
	 * 
	 * @return List of the attribute merger Thymeleaf test files.
	 */
	@Parameters(name = '{0}')
	static List<String> listAttributeMergerTestFiles() {

		return new Reflections('', new ResourcesScanner()).getResources(~/AttributeMerger.*\.thtest/) as List
	}


	/**
	 * Basic dialect with just the attribute merger test processor.
	 */
	private class AttributeMergerTestDialect extends AbstractProcessorDialect {

		static {
			ModelExtensions.apply()
		}

		AttributeMergerTestDialect() {
			super('Attribute Merger Test Dialect', 'layout-test', 10)
		}

		@Override
		Set<? extends IProcessor> getProcessors(String dialectPrefix) {
			return [
				new AttributeMergerProcessor(dialectPrefix)
			]
		}
	}


	/**
	 * Basic processor which just invokes the attribute merger.
	 */
	private class AttributeMergerProcessor extends AbstractAttributeTagProcessor {

		AttributeMergerProcessor(String dialectPrefix) {
			super(TemplateMode.HTML, dialectPrefix, null, false, 'attribute-merger', true, 0, true)
		}

		@Override
		protected void doProcess(ITemplateContext context, IProcessableElementTag tag,
			AttributeName attributeName, String attributeValue, IElementTagStructureHandler structureHandler) {

			def modelFactory = context.modelFactory
			def targetModel = new ModelFinder(context, templateMode).findTemplate(attributeValue)
			def sourceModel = modelFactory.createModel(tag)

			new AttributeMerger().merge(modelFactory, targetModel, sourceModel)

			targetModel.replace(0, modelFactory.removeAttribute(targetModel.get(0), 'layout-test', 'attribute-merger'))
			structureHandler.replaceWith(targetModel, false)
		}
	}
}
