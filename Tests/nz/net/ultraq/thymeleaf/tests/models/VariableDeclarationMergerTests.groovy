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

import nz.net.ultraq.thymeleaf.models.VariableDeclarationMerger

import org.junit.Test
import static org.junit.Assert.*

/**
 * Tests for the variable declaration merger.
 * 
 * @author Emanuel Rabina
 */
class VariableDeclarationMergerTests {

	/**
	 * Test that the merger just concatenates values when none of the names in the
	 * source and target match.
	 */
	@Test
	void noMerges() {

		def target = 'name1=${value1}'
		def source = 'name2=${value2}'

		def merger = new VariableDeclarationMerger()
		def result = merger.merge(target, source)

		assertEquals('name1=${value1},name2=${value2}', result)
	}

	/**
	 * Test that the merger replaces the target value because the source value has
	 * the same name.
	 */
	@Test
	void basicMerge() {

		def target = 'name=${value1}'
		def source = 'name=${value2}'

		def merger = new VariableDeclarationMerger()
		def result = merger.merge(target, source)

		assertEquals('name=${value2}', result)
	}

	/**
	 * Test that the merger replaces just the target value with the same name in
	 * the source, leaving the rest of the target string alone.
	 */
	@Test
	void mixedMerge() {

		def target = 'name1=${value1},name2=${value2}'
		def source = 'name1=${newValue},name3=${value3}'

		def merger = new VariableDeclarationMerger()
		def result = merger.merge(target, source)

		assertEquals('name1=${newValue},name2=${value2},name3=${value3}', result)
	}

	/**
	 * Test that the merger just uses the target value when no source is present.
	 */
	@Test
	void noSource() {

		def target = 'name=${value}'
		def source = null

		def merger = new VariableDeclarationMerger()
		def result = merger.merge(target, source)

		assertEquals(target, result)
	}

	/**
	 * Test that the merger just uses the source value when no target is present.
	 */
	@Test
	void noTarget() {

		def target = null
		def source = 'name=${value}'

		def merger = new VariableDeclarationMerger()
		def result = merger.merge(target, source)

		assertEquals(source, result)
	}
}
