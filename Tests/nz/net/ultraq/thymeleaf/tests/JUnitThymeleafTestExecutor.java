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

package nz.net.ultraq.thymeleaf.tests;

import nz.net.ultraq.thymeleaf.LayoutDialect;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.thymeleaf.standard.StandardDialect;
import org.thymeleaf.testing.templateengine.engine.TestExecutor;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import static org.junit.Assert.assertTrue;

/**
 * A parameterized JUnit test class that is run over every Thymeleaf testing
 * file (.thtest) in the test directory.
 * 
 * @author Emanuel Rabina
 */
@RunWith(Parameterized.class)
public class JUnitThymeleafTestExecutor {

	private static final String TEST_FILE_DIRECTORY = "nz/net/ultraq/thymeleaf/tests/";
	private static final String TEST_FILE_PREFIX    = "classpath:" + TEST_FILE_DIRECTORY;
	private static final String TEST_FILE_EXTENSION = ".thtest";

	private static TestExecutor testexecutor;
	private static JUnitTestReporter testreporter;
	static {
		testexecutor = new TestExecutor();
		testexecutor.setDialects(Arrays.asList(new StandardDialect(), new LayoutDialect()));

		testreporter = new JUnitTestReporter();
		testexecutor.setReporter(testreporter);
	}

	private final String testName;

	/**
	 * Constructor, build a test over the given Thymeleaf testing file.
	 * 
	 * @param testName
	 */
	public JUnitThymeleafTestExecutor(String testName) {

		this.testName = testName;
	}

	/**
	 * Run the Thymeleaf test executor over the test file, asserting the result
	 * was OK (execution result matched the expected output).
	 */
	@Test
	public void executeTestFile() {

		testexecutor.execute(TEST_FILE_PREFIX + testName);
		assertTrue(testreporter.getLastResult().isOK());
	}

	/**
	 * Get all the <tt>.thtest</tt> files in the project classpath.
	 * 
	 * @return List of all the Thymeleaf testing files.
	 * @throws URISyntaxException
	 */
	@Parameters(name = "{0}")
	public static Collection<Object[]> thymeleafTestFiles() throws URISyntaxException {

		URL testingDirectoryUrl = JUnitThymeleafTestExecutor.class.getClassLoader().getResource(TEST_FILE_DIRECTORY);
		ArrayList<File> testFiles = thymeleafTestFiles(new File(testingDirectoryUrl.toURI()));

		ArrayList<Object[]> parameters = new ArrayList<Object[]>();
		for (File testFile: testFiles) {
			String testFilePath = testFile.getAbsolutePath().replace('\\', '/');
			testFilePath = testFilePath.substring(testFilePath.indexOf(TEST_FILE_DIRECTORY))
					.substring(TEST_FILE_DIRECTORY.length());
			parameters.add(new Object[]{ testFilePath });
		}
		return parameters;
	}

	/**
	 * Recursively gather all the <tt>.thtest</tt> files in the given directory.
	 * 
	 * @param directory
	 * @return List of Thymeleaf testing files.
	 */
	private static ArrayList<File> thymeleafTestFiles(File directory) {

		ArrayList<File> testingFiles = new ArrayList<File>();
		for (File file: directory.listFiles()) {
			if (file.isDirectory()) {
				testingFiles.addAll(thymeleafTestFiles(file));
			}
			else if (file.getName().endsWith(TEST_FILE_EXTENSION)) {
				testingFiles.add(file);
			}
		}
		return testingFiles;
	}
}
