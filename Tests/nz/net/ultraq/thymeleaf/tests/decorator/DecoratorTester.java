/*
 * Copyright 2013, Emanuel Rabina (http://www.ultraq.net.nz/)
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

package nz.net.ultraq.thymeleaf.tests.decorator;

import nz.net.ultraq.thymeleaf.tests.AbstractLayoutDialectTester;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * JUnit test class containing tests related to the <tt>layout:decorator</tt>
 * attribute processor.
 * 
 * @author Emanuel Rabina
 */
public class DecoratorTester extends AbstractLayoutDialectTester {

	/**
	 * Test decoration of a page with both head and body content, using a
	 * full-page layout.
	 */
	@Test
	public void fullLayoutFullContent() {

		testexecutor.execute("decorator/FullLayout-FullContent");
		assertTrue(lastTestResult().isOK());
	}

	/**
	 * Test decoration of a page with head content only, using a full-page
	 * layout.
	 */
	@Test
	public void fullLayoutHeadOnlyContent() {

		testexecutor.execute("decorator/FullLayout-HeadOnlyContent");
		assertTrue(lastTestResult().isOK());
	}

	/**
	 * Test decoration of a page with body content only, using a full-page
	 * layout.
	 */
	@Test
	public void fullLayoutBodyOnlyContent() {

		testexecutor.execute("decorator/FullLayout-BodyOnlyContent");
		assertTrue(lastTestResult().isOK());
	}

	/**
	 * Test decoration of a page with both head and body content, using a
	 * head-only layout.
	 */
	@Test
	public void headOnlyLayoutFullContent() {

		testexecutor.execute("decorator/HeadOnlyLayout-FullContent");
		assertTrue(lastTestResult().isOK());
	}

	/**
	 * Test decoration of a page with head content only, using a head-only
	 * layout.
	 */
	@Test
	public void headOnlyLayoutHeadOnlyContent() {

		testexecutor.execute("decorator/HeadOnlyLayout-HeadOnlyContent");
		assertTrue(lastTestResult().isOK());
	}

	/**
	 * Test decoration of a page with body content only, using a head-only
	 * layout.
	 */
	@Test
	public void headOnlyLayoutBodyOnlyContent() {

		testexecutor.execute("decorator/HeadOnlyLayout-BodyOnlyContent");
		assertTrue(lastTestResult().isOK());
	}

	/**
	 * Test decoration of a page with both head and body content, using a
	 * body-only layout.
	 */
	@Test
	public void bodyOnlyLayoutFullContent() {

		testexecutor.execute("decorator/BodyOnlyLayout-FullContent");
		assertTrue(lastTestResult().isOK());
	}

	/**
	 * Test decoration of a page with head content only, using a body-only
	 * layout.
	 */
	@Test
	public void bodyOnlyLayoutHeadOnlyContent() {

		testexecutor.execute("decorator/BodyOnlyLayout-HeadOnlyContent");
		assertTrue(lastTestResult().isOK());
	}

	/**
	 * Test decoration of a page with body content only, using a body-only
	 * layout.
	 */
	@Test
	public void bodyOnlyLayoutBodyOnlyContent() {

		testexecutor.execute("decorator/BodyOnlyLayout-BodyOnlyContent");
		assertTrue(lastTestResult().isOK());
	}
}
