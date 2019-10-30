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

import org.junit.Before
import org.junit.Test
import org.thymeleaf.cache.ICacheEntryValidity
import org.thymeleaf.context.ITemplateContext
import static nz.net.ultraq.thymeleaf.fragments.extensions.FragmentExtensions.FRAGMENT_COLLECTION_KEY

import org.thymeleaf.engine.TemplateData
import org.thymeleaf.templatemode.TemplateMode
import org.thymeleaf.templateresource.ITemplateResource
import static org.mockito.Mockito.*

/**
 * Tests for extensions made to Thymeleaf classes for working with fragments.
 * 
 * @author Emanuel Rabina
 */
class FragmentExtensionsTests {

	private ITemplateContext context

	/**
	 * Context setup.
	 */
	@Before
	void newContext() {

		context = mock(ITemplateContext)
	}

	/**
	 * Test that contexts without a fragment map get a new one.
	 */
	@Test
	void getNewMap() {

		assert context.variableNames.empty

		def newMap = context.fragmentCollection
		assert newMap != null
	}

	/**
	 * Test that an existing map is returned if available.
	 */
	@Test
	void getExistingMap() {

		def existingMap = [:]
		when(context[FRAGMENT_COLLECTION_KEY]).thenReturn(existingMap)

		def map = context.fragmentCollection
		assert map.is(existingMap)
	}

	/**
	 * Test that a new map is always returned when we think we're running a fresh
	 * decorator process (the template stack size is 1 and we're in the context of
	 * the decorator).
	 */
	@Test
	void getNewMapWhenRunningAFreshDecorator() {

		def existingMap = [:]
		when(context[FRAGMENT_COLLECTION_KEY]).thenReturn(existingMap)
		when(context.templateStack).thenReturn([new TemplateData('template', [] as Set,
			mock(ITemplateResource), TemplateMode.HTML, mock(ICacheEntryValidity))])

		def map = context.getFragmentCollection(true)
		assert !map.is(existingMap)
	}
}
