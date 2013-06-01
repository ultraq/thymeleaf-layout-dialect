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

import org.thymeleaf.testing.templateengine.report.ConsoleTestReporter;
import org.thymeleaf.testing.templateengine.testable.ITest;
import org.thymeleaf.testing.templateengine.testable.ITestResult;

/**
 * Provides access to the last test result so it can be interrogated by JUnit.
 * 
 * @author Emanuel Rabina
 */
public class JUnitTestReporter extends ConsoleTestReporter {

	private ITestResult lastresult;

	/**
	 * Return the result of the last test execution.
	 * 
	 * @return The last test result.
	 */
	ITestResult getLastResult() {

		return lastresult;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void reportTestEnd(String executionId, int nestingLevel, ITest test, String testName,
		ITestResult result, long executionTimeNanos) {

		lastresult = result;
		super.reportTestEnd(executionId, nestingLevel, test, testName, result, executionTimeNanos);
	}
}
