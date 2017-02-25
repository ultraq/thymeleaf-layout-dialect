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

	private static final String DIALECT_PREFIX_CACHE = 'DialectPrefixCache'

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
			 * Returns the configured prefix for the given dialect.  If the dialect
			 * prefix has not been configured, then the dialect prefix is returned.
			 * 
			 * @param dialectClass
			 * @return The configured prefix for the dialect, or {@code null} if the
			 *         dialect being queried hasn't been configured.
			 */
			getPrefixForDialect << { Class<IProcessorDialect> dialectClass ->
				def dialectPrefixCache = delegate[DIALECT_PREFIX_CACHE]
				if (!dialectPrefixCache) {
					dialectPrefixCache = [:]
					delegate[DIALECT_PREFIX_CACHE] = dialectPrefixCache
				}
				def dialectPrefix = dialectPrefixCache[dialectClass]
				if (!dialectPrefix) {
					def dialectConfiguration = delegate.configuration.dialectConfigurations.find { dialectConfig ->
						return dialectClass.isInstance(dialectConfig.dialect)
					}
					dialectPrefix = dialectConfiguration ?
						dialectConfiguration.prefixSpecified ? dialectConfiguration.prefix : dialectConfiguration.dialect.prefix :
						null
					dialectPrefixCache[dialectClass] = dialectPrefix
				}
				return dialectPrefix
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
