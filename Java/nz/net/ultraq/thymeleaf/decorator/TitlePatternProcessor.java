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

package nz.net.ultraq.thymeleaf.decorator;

import static nz.net.ultraq.thymeleaf.LayoutUtilities.HTML_ELEMENT_TITLE;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.Arguments;
import org.thymeleaf.Configuration;
import org.thymeleaf.dom.Element;
import org.thymeleaf.dom.Text;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.processor.ProcessorResult;
import org.thymeleaf.processor.attr.AbstractAttrProcessor;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.StandardExpressions;
import org.thymeleaf.util.StringUtils;

/**
 * Allows for greater control of the resulting &lt;title&gt; element by
 * specifying a pattern with some special tokens.  This can be used to extend
 * the decorator's title with the content's one, instead of simply overriding
 * it.
 * 
 * @author Emanuel Rabina
 */
public class TitlePatternProcessor extends AbstractAttrProcessor {

	private static final Logger logger = LoggerFactory.getLogger(TitlePatternProcessor.class);

	private static final String PARAM_TITLE_DECORATOR = "$DECORATOR_TITLE";
	private static final String PARAM_TITLE_CONTENT   = "$CONTENT_TITLE";

	public static final String PROCESSOR_NAME_TITLEPATTERN = "title-pattern";

	public static final String DECORATOR_TITLE = "title-pattern::decorator-title";
	public static final String CONTENT_TITLE   = "title-pattern::content-title";

	/**
	 * Constructor, sets this processor to work on the 'title-pattern' attribute.
	 */
	public TitlePatternProcessor() {

		super(PROCESSOR_NAME_TITLEPATTERN);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getPrecedence() {

		return 1;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected ProcessorResult processAttribute(Arguments arguments, Element element, String attributeName) {

		// Ensure this attribute is only on the <title> element
		if (!element.getNormalizedName().equals(HTML_ELEMENT_TITLE)) {
			logger.error("layout:title-pattern attribute should only appear in a <title> element");
			throw new IllegalArgumentException("layout:title-pattern attribute should only appear in a <title> element");
		}

		Configuration configuration = arguments.getConfiguration();
		IStandardExpressionParser parser = StandardExpressions.getExpressionParser(configuration);

		// Process the decorator and content title parts
		String decoratortitle = processTitle((String)element.getNodeProperty(DECORATOR_TITLE),
				arguments, configuration, parser);
		String contenttitle = processTitle((String)element.getNodeProperty(CONTENT_TITLE),
				arguments, configuration, parser);

		// Replace the <title> text with an expanded title pattern expression,
		// only using the pattern if both the decorator and content have a title.
		String titlepattern = element.getAttributeValue(attributeName);
		String title =
			decoratortitle != null ? contenttitle != null ?
				titlepattern
					.replace(PARAM_TITLE_DECORATOR, decoratortitle)
					.replace(PARAM_TITLE_CONTENT,   contenttitle) :
				decoratortitle :
			contenttitle != null ? contenttitle : "";

		element.clearChildren();
		element.addChild(new Text(title));
		element.removeAttribute(attributeName);

		return ProcessorResult.OK;
	}

	/**
	 * Process a title part, executing expressions if it contains any.
	 * 
	 * @param title
	 * @param arguments
	 * @param configuration
	 * @param parser
	 * @return The result of executing <tt>title</tt> if it contained any
	 *         expressions, or <tt>null</tt> if the title resolved to an empty
	 *         string or whitespace.
	 */
	private static String processTitle(String title, Arguments arguments,
		Configuration configuration, IStandardExpressionParser parser) {

		String titlevalue;
		try {
			titlevalue = title == null ? null :
					parser.parseExpression(configuration, arguments, title)
						.execute(configuration, arguments)
						.toString();
		}
		catch (TemplateProcessingException ex) {
			titlevalue = title;
		}
		titlevalue = StringUtils.isEmptyOrWhitespace(titlevalue) ? null : titlevalue.trim();

		return titlevalue;
	}
}
