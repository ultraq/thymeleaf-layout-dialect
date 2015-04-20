/*
 * Copyright 2014, Emanuel Rabina (http://www.ultraq.net.nz/)
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

package nz.net.ultraq.thymeleaf.tests

import nz.net.ultraq.thymeleaf.LayoutDialect

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters
import org.thymeleaf.standard.StandardDialect
import org.thymeleaf.testing.templateengine.engine.TestExecutor
import org.thymeleaf.testing.templateengine.report.ConsoleTestReporter
import org.thymeleaf.testing.templateengine.report.ITestReporter
import static org.junit.Assert.assertTrue

import groovy.transform.Immutable

/**
 * A parameterized JUnit test class that is run over every Thymeleaf testing
 * file (.thtest) in the test directory.
 * 
 * @author Emanuel Rabina
 */
@Immutable
@RunWith(Parameterized.class)
public class JUnitThymeleafTestExecutor {

	private static final String TEST_FILE_DIRECTORY = "nz/net/ultraq/thymeleaf/tests/"
	private static final String TEST_FILE_PREFIX    = "classpath:" + TEST_FILE_DIRECTORY
	private static final String TEST_FILE_EXTENSION = ".thtest"

	private static TestExecutor testExecutor
	private static ITestReporter testReporter

	static {
		testExecutor = new TestExecutor()
		testExecutor.dialects = [ new StandardDialect(), new LayoutDialect() ]

		// Use a modified ConsoleTestReporter to make it more useful for JUnit
		testReporter = new ConsoleTestReporter()
		testReporter.metaClass {
			lastResult = null
			testEnd = { executionId, nestingLevel, test, testName, result, executionTimeNanos ->
				lastResult = result
				delegate.testEnd(executionId, nestingLevel, test, testName, result, executionTimeNanos)
			}
		}
		testExecutor.setReporter(testReporter)
	}

	private final String testName

	/**
	 * Run the Thymeleaf test executor over the test file, asserting the result
	 * was OK (execution result matched the expected output).
	 */
	@Test
	void executeTestFile() {

		testExecutor.execute(TEST_FILE_PREFIX + testName)
		assertTrue(testReporter.lastResult.isOK())
	}

	/**
	 * Get all the <tt>.thtest</tt> files in the project classpath.
	 * 
	 * @return List of all the Thymeleaf testing files.
	 * @throws URISyntaxException
	 */
	@Parameters(name = "{0}")
	static Collection<Object[]> thymeleafTestFiles() throws URISyntaxException {

		// Gather all the <tt>.thtest</tt> files in the given directory and all its subdirectories
		def findAllTestFiles = { directory ->
			def testingFiles = []
			directory.listFiles().each { file ->
				if (file.directory) {
					testingFiles += findAllTestFiles(file)
				}
				else if (file.name.endsWith(TEST_FILE_EXTENSION)) {
					testingFiles << file
				}
			}
			return testingFiles
		}

		def testingDirectoryUrl = JUnitThymeleafTestExecutor.classLoader.getResource(TEST_FILE_DIRECTORY)
		def testFiles = findAllTestFiles(new File(testingDirectoryUrl.toURI()))

		return testFiles.collect { testFile ->
			def testFilePath = testFile.absolutePath.replace('\\', '/')
			testFilePath = testFilePath.substring(testFilePath.indexOf(TEST_FILE_DIRECTORY))
					.substring(TEST_FILE_DIRECTORY.length())
			return [testFilePath] as Object[]
		}
	}
}
