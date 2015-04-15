/*
 * Copyright 2012, Emanuel Rabina (http://www.ultraq.net.nz/)
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

package nz.net.ultraq.thymeleaf

import nz.net.ultraq.thymeleaf.decorators.DecoratorProcessor
import nz.net.ultraq.thymeleaf.decorators.TitlePatternProcessor
import nz.net.ultraq.thymeleaf.fragments.FragmentProcessor
import nz.net.ultraq.thymeleaf.includes.IncludeProcessor
import nz.net.ultraq.thymeleaf.includes.ReplaceProcessor

import org.thymeleaf.dialect.AbstractDialect
import org.thymeleaf.dom.Attribute
import org.thymeleaf.dom.Element
import org.thymeleaf.processor.IProcessor

/**
 * Dialect for making use of template/layout decorator pages with Thymeleaf.
 * 
 * @author Emanuel Rabina
 */
class LayoutDialect extends AbstractDialect {

	static final String DIALECT_NAMESPACE_LAYOUT = 'http://www.ultraq.net.nz/thymeleaf/layout'
	static final String DIALECT_PREFIX_LAYOUT    = 'layout'

	/**
	 * Add the following methods to Thymeleaf's objects.  Woohoo
	 * metaprogramming! :D
	 */
	static {
		Attribute.metaClass {
			equalsName << { String prefix, String name ->
				return delegate.originalName == "${prefix}:${name}" ?:
				       delegate.originalName == "data-${prefix}-${name}"
			}
		}
		Element.metaClass {
			findElement << { String name ->
				def search = { Element element ->
					if (element.originalName == name) {
						return element
					}
					return element.elementChildren.find(search)
				}
				return search.call(delegate)
			}
			getAttributeValue << { String prefix, String name ->
				return delegate.getAttributeValue("${prefix}:${name}") ?:
				       delegate.getAttributeValue("data-${prefix}-${name}")
			}
			hasAttribute << { String prefix, String name ->
				return delegate.hasAttribute("${prefix}:${name}") ||
				       delegate.hasAttribute("data-${prefix}-${name}")
			}
			removeAttribute << { String prefix, String name ->
				delegate.removeAttribute("${prefix}:${name}")
				delegate.removeAttribute("data-${prefix}-${name}")
			}
		}
	}

	/**
	 * Return the layout prefix.
	 * 
	 * @return <tt>layout</tt>
	 */
	@Override
	String getPrefix() {

		return DIALECT_PREFIX_LAYOUT
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	Set<IProcessor> getProcessors() {

		return [
		    new DecoratorProcessor(),
			new IncludeProcessor(),
			new ReplaceProcessor(),
			new FragmentProcessor(),
			new TitlePatternProcessor()
		]
	}
}
