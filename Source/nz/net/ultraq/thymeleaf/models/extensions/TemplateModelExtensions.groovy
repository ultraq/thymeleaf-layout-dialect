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

package nz.net.ultraq.thymeleaf.models.extensions

import org.thymeleaf.engine.TemplateModel

/**
 * Meta-programming extensions to the {@link TemplateModel} class.
 * 
 * @author Emanuel Rabina
 */
class TemplateModelExtensions {

	/**
	 * Apply extensions to the {@code TemplateModel} class.
	 */
	static void apply() {

		TemplateModel.metaClass {

			/**
			 * Shortcut to the template name found on the template data object.  Only
			 * works if the template was resolved via a name, rather than a string
			 * (eg: anonymous template), in which case this can return the entire
			 * template!
			 * 
			 * @return Template name.
			 */
			getTemplate << {
				return delegate.templateData.template
			}
		}
	}
}
