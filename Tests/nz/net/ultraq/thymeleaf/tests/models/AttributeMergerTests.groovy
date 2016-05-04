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

package nz.net.ultraq.thymeleaf.tests.models

import nz.net.ultraq.thymeleaf.fragments.mergers.AttributeMerger

import org.junit.Test
import org.thymeleaf.dom.Element
import static org.junit.Assert.*

/**
 * Tests for the attribute merger (outside of testing <tt>th:with</tt> behaviour
 * which is covered by the {@link VariableDeclarationMergerTests} class).
 * 
 * @author Emanuel Rabina
 */
class AttributeMergerTests {

	/**
	 * Test that the merger just adds attributes found in the source to the target
	 * that don't already exist in the target.
	 */
	@Test
	void addAttributes() {

		def target = new Element('div')
		target.attributes = [
			class: 'container'
		]

		def source = new Element('div')
		source.attributes = [
			id: 'test-element'
		]

		def merger = new AttributeMerger()
		merger.merge(target, source)

		assertEquals(target.getAttributeValue('id'), 'test-element')
		assertEquals(target.getAttributeValue('class'), 'container')
	}

	/**
	 * Test that attributes in the source element override those of the target.
	 */
	@Test
	void mergeAttributes() {

		def target = new Element('div')
		target.attributes = [
			class: 'container'
		]

		def source = new Element('div')
		source.attributes = [
			class: 'roflcopter'
		]

		def merger = new AttributeMerger()
		merger.merge(target, source)

		assertEquals(target.getAttributeValue('class'), 'roflcopter')
	}
}
