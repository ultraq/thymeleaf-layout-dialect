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

package nz.net.ultraq.thymeleaf.tests.fragments

import nz.net.ultraq.thymeleaf.fragments.FragmentMap

import org.junit.Before
import org.junit.Test
import org.thymeleaf.context.Context
import org.thymeleaf.context.IContext
import org.thymeleaf.processor.element.IElementModelStructureHandler
import static org.junit.Assert.*
import static org.mockito.Mockito.*

/**
 * Tests for the {@link FragmentMap} utility.
 * 
 * @author Emanuel Rabina
 */
class FragmentMapTests {

	private IContext context

	/**
	 * Context setup.
	 */
	@Before
	void newContext() {

		context = new Context()
	}

	/**
	 * Test that contexts without a fragment map get a new one.
	 */
	@Test
	void newMap() {

		assertNull(context.variableNames.empty)

		def newMap = FragmentMap.get(context)
		assertNotNull(newMap)
	}

	/**
	 * Test that when requested, any existing fragment map will be returned.
	 */
	@Test
	void existingMap() {

		def newMap = FragmentMap.get(context)
		def sameMap = FragmentMap.get(context)
		assertSame(newMap, sameMap)
	}

	/**
	 * Test that setting the fragments for a node creates a new map.
	 */
	@Test
	void setNewMap() {

		def newMap = FragmentMap.get(context)
		FragmentMap.setForNode(context, mock(IElementModelStructureHandler), [:])
		def differentMap = FragmentMap.get(context)
		assertNotSame(newMap, differentMap)
	}
}
