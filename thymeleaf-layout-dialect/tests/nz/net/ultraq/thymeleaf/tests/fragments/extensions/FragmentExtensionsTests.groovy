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

package nz.net.ultraq.thymeleaf.tests.fragments.extensions

import org.thymeleaf.cache.ICacheEntryValidity
import org.thymeleaf.context.ITemplateContext
import nz.net.ultraq.thymeleaf.fragments.extensions.FragmentExtensions

import org.thymeleaf.engine.TemplateData
import org.thymeleaf.templatemode.TemplateMode
import org.thymeleaf.templateresource.ITemplateResource
import spock.lang.Specification

/**
 * Tests for extensions made to Thymeleaf classes for working with fragments.
 * 
 * @author Emanuel Rabina
 */
@SuppressWarnings('PrivateFieldCouldBeFinal')
class FragmentExtensionsTests extends Specification {

	private ITemplateContext context = Mock(ITemplateContext)

	def "Contexts without a fragment collection get a new one"() {
		given:
			context.templateStack >> []

		when:
			def newMap = context.fragmentCollection

		then:
			newMap != null
	}

	def "An existing map is returned if available"() {
		given:
			context.templateStack >> []
			def existingMap = [:]
			context.getVariable(FragmentExtensions.FRAGMENT_COLLECTION_KEY) >> existingMap

		when:
			def map = context.fragmentCollection

		then:
			map.is(existingMap)
	}

	def "A new map is always returned when template stack size == 1 and in decorator call"() {
		given:
			def existingMap = [:]
			context.getVariable(FragmentExtensions.FRAGMENT_COLLECTION_KEY) >> existingMap
			context.templateStack >> [
				new TemplateData('template', [] as Set, Mock(ITemplateResource), TemplateMode.HTML, Mock(ICacheEntryValidity))
			]

		when:
			def map = context.getFragmentCollection(true)

		then:
			!map.is(existingMap)
	}
}
