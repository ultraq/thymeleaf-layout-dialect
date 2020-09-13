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

package nz.net.ultraq.thymeleaf.context.extensions

import org.thymeleaf.context.IContext
import org.thymeleaf.dialect.IProcessorDialect

/**
 * Meta-programming extensions to the {@link IContext} class.
 * 
 * @author Emanuel Rabina
 */
class IContextExtensions {

	private static final String DIALECT_PREFIX_PREFIX = 'DialectPrefix::'

	/**
	 * Enables use of the {@code value = context[key]} syntax over the context
	 * object, is a synonym for the {@code getVariable} method.
	 * 
	 * @param self
	 * @param name Name of the variable on the context to retrieve.
	 * @return The variable value, or {@code null} if the variable isn't
	 *         mapped to anything on the context.
	 */
	static Object getAt(IContext self, String name) {
		return self.getVariable(name)
	}

	/**
	 * Returns the configured prefix for the given dialect.  If the dialect
	 * prefix has not been configured.
	 * 
	 * @param self
	 * @param dialectClass
	 * @return The configured prefix for the dialect, or {@code null} if the
	 *         dialect being queried hasn't been configured.
	 */
	static String getPrefixForDialect(IContext self, Class<IProcessorDialect> dialectClass) {
		return self.getOrCreate(DIALECT_PREFIX_PREFIX + dialectClass.name) { ->
			def dialectConfiguration = self.configuration.dialectConfigurations.find { dialectConfig ->
				return dialectClass.isInstance(dialectConfig.dialect)
			}
			return dialectConfiguration?.prefixSpecified ?
					dialectConfiguration?.prefix :
					dialectConfiguration?.dialect?.prefix
		}
	}

	/**
	 * Enables use of the {@code context[key] = value} syntax over the context
	 * object, is a synonym for the {@code setVariable} method.
	 * 
	 * @param self
	 * @param name Name of the variable to map the value to.
	 * @param value The value to set.
	 */
	static void putAt(IContext self, String name, Object value) {
		self.setVariable(name, value)
	}
}
