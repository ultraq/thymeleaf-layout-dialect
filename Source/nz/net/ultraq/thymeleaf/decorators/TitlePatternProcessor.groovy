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

package nz.net.ultraq.thymeleaf.decorators

import nz.net.ultraq.thymeleaf.LayoutDialectContext
import nz.net.ultraq.thymeleaf.fragments.mergers.AttributeMerger

import org.thymeleaf.Arguments
import org.thymeleaf.dom.Element
import org.thymeleaf.dom.Text
import org.thymeleaf.processor.ProcessorResult
import org.thymeleaf.processor.attr.AbstractAttrProcessor

/**
 * Allows for greater control of the resulting &lt;title&gt; element by
 * specifying a pattern with some special tokens.  This can be used to extend
 * the decorator's title with the content's one, instead of simply overriding
 * it.
 * 
 * @author Emanuel Rabina
 */
class TitlePatternProcessor extends AbstractAttrProcessor {

	private static final String PARAM_TITLE_DECORATOR = '$DECORATOR_TITLE'
	private static final String PARAM_TITLE_CONTENT   = '$CONTENT_TITLE'

	static final String PROCESSOR_NAME_TITLEPATTERN = 'title-pattern'

	static final String TITLE_TYPE           = 'LayoutDialect::TitlePattern::Type'
	static final String TITLE_TYPE_DECORATOR = 'decorator-title'
	static final String TITLE_TYPE_CONTENT   = 'content-title'

	static final String RESULTING_TITLE = "resultingTitle"

	final int precedence = 1

	/**
	 * Constructor, sets this processor to work on the 'title-pattern' attribute.
	 */
	TitlePatternProcessor() {

		super(PROCESSOR_NAME_TITLEPATTERN)
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected ProcessorResult processAttribute(Arguments arguments, Element element, String attributeName) {

		// Ensure this attribute is only on the <title> element
		if (element.normalizedName != 'title') {
			throw new IllegalArgumentException("${attributeName} processor should only appear in a <title> element")
		}

		// Retrieve title values from the expanded <title> sections within this
		// processing container (if any)
		def titlePattern   = element.getAttributeValue(attributeName)
		def titleContainer = element.parent
		def titleElements  = titleContainer?.elementChildren ?: []
		element.removeAttribute(attributeName)

		def findTitleType = { titleType ->
			return { childElement ->
				childElement.getNodeProperty(TITLE_TYPE) == titleType
			}
		}
		def decoratorTitleElement = titleElements.find(findTitleType(TITLE_TYPE_DECORATOR))
		def decoratorTitle        = decoratorTitleElement?.firstChild?.content
		def contentTitleElement   = titleElements.find(findTitleType(TITLE_TYPE_CONTENT))
		def contentTitle          = contentTitleElement?.firstChild?.content

		def attributeMerger = new AttributeMerger()
		attributeMerger.merge(element, decoratorTitleElement)
		attributeMerger.merge(element, contentTitleElement)

		def title = titlePattern && decoratorTitle && contentTitle ?
			titlePattern
				.replace(PARAM_TITLE_DECORATOR, decoratorTitle)
				.replace(PARAM_TITLE_CONTENT, contentTitle) :
			contentTitle ?: decoratorTitle ?: ''

		// If there's a title, bring it up
		if (title) {
			element.addChild(new Text(title))
			titleContainer.parent.insertAfter(titleContainer, element.cloneNode(null, false))
			LayoutDialectContext.forContext(arguments.context) << [(RESULTING_TITLE): title]
		}

		// Remove the processing section
		titleContainer.parent.removeChild(titleContainer)

		return ProcessorResult.OK
	}
}
