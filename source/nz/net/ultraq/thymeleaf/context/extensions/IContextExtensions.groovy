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
	 * Apply extensions to the {@code IContext} class.
	 */
	static void apply() {

		IContext.metaClass {

			/**
			 * Enables use of the {@code value = context[key]} syntax over the context
			 * object, is a synonym for the {@code getVariable} method.
			 * 
			 * @param name Name of the variable on the context to retrieve.
			 * @return The variable value, or {@code null} if the variable isn't
			 *         mapped to anything on the context.
			 */
			getAt << { String name ->
				return delegate.getVariable(name)
			}

			/**
			 * Retrieves an item from the context, or creates one on the context if it
			 * doesn't yet exist.
			 * 
			 * @param key
			 * @param closure
			 * @return The item cached on the context through the given key, or first
			 *         constructed through the closure.
			 */
			getOrCreate << { String key, Closure closure ->
				def value = delegate[key]
				if (!value) {
					value = closure()
					delegate[key] = value
				}
				return value
			}

			/**
			 * Returns the configured prefix for the given dialect.  If the dialect
			 * prefix has not been configured.
			 * 
			 * @param dialectClass
			 * @return The configured prefix for the dialect, or {@code null} if the
			 *         dialect being queried hasn't been configured.
			 */
			getPrefixForDialect << { Class<IProcessorDialect> dialectClass ->
				return delegate.getOrCreate(DIALECT_PREFIX_PREFIX + dialectClass.name) { ->
					def dialectConfiguration = delegate.configuration.dialectConfigurations.find { dialectConfig ->
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
			 * @param name Name of the variable to map the value to.
			 * @param value The value to set.
			 */
			putAt << { String name, Object value ->
				delegate.setVariable(name, value)
			}
		}
	}
}
