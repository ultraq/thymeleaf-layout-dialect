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
import nz.net.ultraq.thymeleaf.decorators.strategies.AppendingStrategy
import nz.net.ultraq.thymeleaf.decorators.strategies.SortingStrategy
import nz.net.ultraq.thymeleaf.fragments.FragmentProcessor
import nz.net.ultraq.thymeleaf.includes.IncludeProcessor
import nz.net.ultraq.thymeleaf.includes.ReplaceProcessor

import org.thymeleaf.dialect.AbstractDialect
import org.thymeleaf.dom.Attribute
import org.thymeleaf.dom.Document
import org.thymeleaf.dom.Element
import org.thymeleaf.dom.Text
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
	 * Add the following methods to Thymeleaf's DOM objects.  Woohoo
	 * metaprogramming! :D
	 */
	static {
		Attribute.metaClass {

			/**
			 * Returns whether or not an attribute is an attribute processor of
			 * the given name, checks both prefix:processor and
			 * data-prefix-processor variants.
			 * 
			 * @param prefix
			 * @param name
			 * @return <tt>true</tt> if this attribute is an attribute processor
			 *         of the matching name.
			 */
			equalsName << { String prefix, String name ->
				return delegate.originalName == "${prefix}:${name}" ?:
				       delegate.originalName == "data-${prefix}-${name}"
			}
		}

		Element.metaClass {

			/**
			 * Searches this and all children of this element for an element of
			 * the given name.
			 * 
			 * @param name
			 * @return The matching element, or <tt>null</tt> if no match was
			 *         found.
			 */
			findElement << { String name ->
				def search
				search = { element ->
					if (element.originalName == name) {
						return element
					}
					return element.elementChildren.find(search)
				}
				return search(delegate)
			}

			/**
			 * Returns an attribute processor's value, checks both
			 * prefix:processor and data-prefix-processor variants.
			 * 
			 * @param prefix
			 * @param name
			 * @return The value of the matching processor, or <tt>null</tt> if
			 *         the processor doesn't exist on the element.
			 */
			getAttributeValue << { String prefix, String name ->
				return delegate.getAttributeValue("${prefix}:${name}") ?:
				       delegate.getAttributeValue("data-${prefix}-${name}")
			}

			/**
			 * Returns whether or not the element has an attribute processor,
			 * checks both prefix:processor and data-prefix-processor variants.
			 * 
			 * @param prefix
			 * @param name
			 * @return <tt>true</tt> if the processor exists on the element.
			 */
			hasAttribute << { String prefix, String name ->
				return delegate.hasAttribute("${prefix}:${name}") ||
				       delegate.hasAttribute("data-${prefix}-${name}")
			}

			/**
			 * Inserts a child node, creating a whitespace node before it so
			 * that it appears in line with all the existing children.
			 * 
			 * @param child Node to add.
			 * @param index Node position.
			 */
			insertChildWithWhitespace << { child, int index ->
				if (child) {
					def parent = delegate.parent
					def whitespace
					if (parent instanceof Document) {
						whitespace = new Text('\n\t')
					}
					else {
						def parentChildren = parent.children
						whitespace = new Text(parentChildren.get(parentChildren.indexOf(delegate) - 1).content + '\t')
					}
					delegate.insertChild(index, whitespace)
					delegate.insertChild(index + 1, child)
				}
			}

			/**
			 * Removes an attribute processor from this element, checks both
			 * prefix:processor and data-prefix-processor variants.
			 * 
			 * @param prefix
			 * @param name
			 */
			removeAttribute << { String prefix, String name ->
				delegate.removeAttribute("${prefix}:${name}")
				delegate.removeAttribute("data-${prefix}-${name}")
			}

			/**
			 * Removes a child node and the whitespace node immediately before
			 * so that the area doesn't appear too messy.
			 * 
			 * @param child Node to remove
			 */
			removeChildWithWhitespace << { child ->
				if (child) {
					def children = delegate.children
					def index = children.indexOf(child)
					delegate.removeChild(index)
					delegate.removeChild(index - 1)
				}
			}
		}
	}

	private final SortingStrategy sortingStrategy

	final String prefix = DIALECT_PREFIX_LAYOUT
	final Set<IProcessor> processors = [
		new DecoratorProcessor(sortingStrategy),
		new IncludeProcessor(),
		new ReplaceProcessor(),
		new FragmentProcessor(),
		new TitlePatternProcessor()
	]

	/**
	 * Constructor, configure the layout dialect with the given values.
	 * 
	 * @param sortingStrategy
	 */
	LayoutDialect(SortingStrategy sortingStrategy = new AppendingStrategy()) {

		this.sortingStrategy = sortingStrategy
	}
}
