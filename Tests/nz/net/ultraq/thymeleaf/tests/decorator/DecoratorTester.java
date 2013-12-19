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

		testOK("decorator/FullLayout-FullContent");
	}

	/**
	 * Test decoration of a page with head content only, using a full-page
	 * layout.
	 */
	@Test
	public void fullLayoutHeadOnlyContent() {

		testOK("decorator/FullLayout-HeadOnlyContent");
	}

	/**
	 * Test decoration of a page with body content only, using a full-page
	 * layout.
	 */
	@Test
	public void fullLayoutBodyOnlyContent() {

		testOK("decorator/FullLayout-BodyOnlyContent");
	}

	/**
	 * Test decoration of a page with both head and body content, using a
	 * head-only layout.
	 */
	@Test
	public void headOnlyLayoutFullContent() {

		testOK("decorator/HeadOnlyLayout-FullContent");
	}

	/**
	 * Test decoration of a page with head content only, using a head-only
	 * layout.
	 */
	@Test
	public void headOnlyLayoutHeadOnlyContent() {

		testOK("decorator/HeadOnlyLayout-HeadOnlyContent");
	}

	/**
	 * Test decoration of a page with body content only, using a head-only
	 * layout.
	 */
	@Test
	public void headOnlyLayoutBodyOnlyContent() {

		testOK("decorator/HeadOnlyLayout-BodyOnlyContent");
	}

	/**
	 * Test decoration of a page with both head and body content, using a
	 * body-only layout.
	 */
	@Test
	public void bodyOnlyLayoutFullContent() {

		testOK("decorator/BodyOnlyLayout-FullContent");
	}

	/**
	 * Test decoration of a page with head content only, using a body-only
	 * layout.
	 */
	@Test
	public void bodyOnlyLayoutHeadOnlyContent() {

		testOK("decorator/BodyOnlyLayout-HeadOnlyContent");
	}

	/**
	 * Test decoration of a page with body content only, using a body-only
	 * layout.
	 */
	@Test
	public void bodyOnlyLayoutBodyOnlyContent() {

		testOK("decorator/BodyOnlyLayout-BodyOnlyContent");
	}

	/**
	 * Test decoration of a page that is an HTML fragment.
	 */
	@Test
	public void fragmentDecoration() {

		testOK("decorator/FragmentDecoration");
	}

	/**
	 * Test the merging of the DOCTYPE, so that the result always tries to have
	 * one.
	 */
	@Test
	public void doctypeMergingContentNoDocType() {

		testOK("decorator/DocTypeMerging-ContentNoDocType");
	}

	/**
	 * Test the merging of the DOCTYPE, so that the result always tries to have
	 * one.
	 */
	@Test
	public void doctypeMergingLayoutNoDocType() {

		testOK("decorator/DocTypeMerging-LayoutNoDocType");
	}

	/**
	 * Test the merging of the DOCTYPE, so that the result always tries to have
	 * one.
	 */
	@Test
	public void doctypeMergingNoDocTypes() {

		testOK("decorator/DocTypeMerging-NoDocTypes");
	}

	/**
	 * Test that the decorator processor must appear in the root element only.
	 */
	@Test
	public void appearInRootElementOnly() {

		testOK("decorator/AppearInRootElementOnly");
	}

	/**
	 * Test whether comments found outside the HTML element are included in the
	 * final output.
	 */
	@Test
	public void externalComments() {

		testOK("decorator/ExternalComments");
	}

	/**
	 * Test that standard decoration can occur on XML documents.
	 */
	@Test
	public void xmlDecoration() {

		testOK("decorator/XMLDecoration");
	}

	/**
	 * Test the decorator using the 'legacy HTML5' template mode.
	 */
	@Test
	public void nonXmlHtml5() {

		testOK("decorator/NonXMLHTML5");
	}

	/**
	 * Test a deep layout hierarchy (3 levels).
	 */
	@Test
	public void deepHierarchy() {

		testOK("decorator/DeepHierarchy");
	}
}
