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

package nz.net.ultraq.thymeleaf.tests;

import nz.net.ultraq.thymeleaf.LayoutDialect;

import org.junit.BeforeClass;
import org.thymeleaf.standard.StandardDialect;
import org.thymeleaf.testing.templateengine.engine.TestExecutor;
import org.thymeleaf.testing.templateengine.report.ConsoleTestReporter;

import static org.junit.Assert.*;

import java.util.Arrays;

/**
 * Common setup code to test the layout dialect using the Thymeleaf Testing
 * library.
 * 
 * @author Emanuel Rabina
 */
public abstract class AbstractLayoutDialectTester {

	private static final String TEST_FILE_PREFIX = "classpath:nz/net/ultraq/thymeleaf/tests/";
	private static final String TEST_FILE_SUFFIX = ".thtest";

	private static TestExecutor testexecutor;

	/**
	 * Set up the test executor to include the layout dialect.
	 */
	@BeforeClass
	public static void initTestExecutor() {

		testexecutor = new TestExecutor();
		testexecutor.setDialects(Arrays.asList(new StandardDialect(), new LayoutDialect()));
		testexecutor.setReporter(new ConsoleTestReporter());
	}

	/**
	 * Perform a standard test on the given Thymeleaf test file.
	 * 
	 * @param testfile
	 */
	protected void testOK(String testfile) {

		testexecutor.execute(TEST_FILE_PREFIX + testfile + TEST_FILE_SUFFIX);
		assertTrue(testexecutor.isAllOK());
		testexecutor.reset();
	}
}
