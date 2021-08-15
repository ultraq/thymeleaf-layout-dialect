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

package nz.net.ultraq.thymeleaf.layoutdialect.models.extensions

import org.thymeleaf.model.ICloseElementTag
import org.thymeleaf.model.IOpenElementTag
import org.thymeleaf.model.IStandaloneElementTag
import org.thymeleaf.model.ITemplateEvent
import org.thymeleaf.model.IText

/**
 * Meta-programming extensions to the {@link ITemplateEvent} class.
 * 
 * @author Emanuel Rabina
 */
class ITemplateEventExtensions {

	/**
	 * Returns whether or not this event represents an opening element.
	 * 
	 * @param self
	 * @return {@code true} if this event is an opening tag.
	 */
	static boolean isClosingElement(ITemplateEvent self) {
		return self instanceof ICloseElementTag || self instanceof IStandaloneElementTag
	}

	/**
	 * Returns whether or not this event represents a closing element of the
	 * given name.
	 * 
	 * @param self
	 * @param tagName
	 * @return {@code true} if this event is a closing tag and has the given
	 *         tag name.
	 */
	static boolean isClosingElementOf(ITemplateEvent self, String tagName) {
		return self.closingElement && self.elementCompleteName == tagName
	}

	/**
	 * Returns whether or not this event represents an opening element.
	 * 
	 * @param self
	 * @return {@code true} if this event is an opening tag.
	 */
	static boolean isOpeningElement(ITemplateEvent self) {
		return self instanceof IOpenElementTag || self instanceof IStandaloneElementTag
	}

	/**
	 * Returns whether or not this event represents an opening element of the
	 * given name.
	 * 
	 * @param self
	 * @param tagName
	 * @return {@code true} if this event is an opening tag and has the given
	 *         tag name.
	 */
	static boolean isOpeningElementOf(ITemplateEvent self, String tagName) {
		return self.openingElement && self.elementCompleteName == tagName
	}

	/**
	 * Returns whether or not this event represents collapsible whitespace.
	 * 
	 * @param self
	 * @return {@code true} if this is a collapsible text node.
	 */
	static boolean isWhitespace(ITemplateEvent self) {
		return self instanceof IText && self.whitespace
	}
}
