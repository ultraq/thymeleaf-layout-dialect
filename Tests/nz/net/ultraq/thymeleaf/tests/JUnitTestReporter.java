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

import org.thymeleaf.testing.templateengine.report.AbstractTestReporter;
import org.thymeleaf.testing.templateengine.report.ConsoleTestReporter;
import org.thymeleaf.testing.templateengine.testable.ITest;
import org.thymeleaf.testing.templateengine.testable.ITestIterator;
import org.thymeleaf.testing.templateengine.testable.ITestParallelizer;
import org.thymeleaf.testing.templateengine.testable.ITestResult;
import org.thymeleaf.testing.templateengine.testable.ITestSequence;

/**
 * Provides access to the last test result so it can be interrogated by JUnit.
 * Just wraps the {@link ConsoleTestReporter} otherwise.
 * 
 * @author Emanuel Rabina
 */
public class JUnitTestReporter extends AbstractTestReporter {

	private final ConsoleTestReporter testreporter = new ConsoleTestReporter();
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
	public void iterationEnd(String executionId, int nestingLevel, ITestIterator iterator,
		int iterationNumber, int okTests, int totalTests, long executionTimeNanos) {

		testreporter.iterationEnd(executionId, nestingLevel, iterator, iterationNumber,
				okTests, totalTests, executionTimeNanos);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void iterationStart(String executionId, int nestingLevel, ITestIterator iterator,
		int iterationNumber) {

		testreporter.iterationStart(executionId, nestingLevel, iterator, iterationNumber);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void iteratorEnd(String executionId, int nestingLevel, ITestIterator iterator,
		int okTests, int totalTests, long executionTimeNanos) {

		testreporter.iteratorEnd(executionId, nestingLevel, iterator, okTests, totalTests,
				executionTimeNanos);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void iteratorStart(String executionId, int nestingLevel, ITestIterator iterator) {

		testreporter.iteratorStart(executionId, nestingLevel, iterator);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void parallelizerEnd(String executionId, int nestingLevel, ITestParallelizer parallelizer,
		int okTests, int totalTests, long executionTimeNanos) {

		testreporter.parallelizerEnd(executionId, nestingLevel, parallelizer, okTests, totalTests,
				executionTimeNanos);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void parallelizerStart(String executionId, int nestingLevel, ITestParallelizer parallelizer) {

		testreporter.parallelizerStart(executionId, nestingLevel, parallelizer);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void parallelThreadEnd(String executionId, int nestingLevel, ITestParallelizer parallelizer,
		int threadNumber, int okTests, int totalTests, long executionTimeNanos) {

		testreporter.parallelThreadEnd(executionId, nestingLevel, parallelizer, threadNumber, okTests,
				totalTests, executionTimeNanos);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void parallelThreadStart(String executionId, int nestingLevel, ITestParallelizer parallelizer,
		int threadNumber) {

		testreporter.parallelThreadStart(executionId, nestingLevel, parallelizer, threadNumber);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void reportTestEnd(String executionId, int nestingLevel, ITest test, String testName,
		ITestResult result, long executionTimeNanos) {

		lastresult = result;
		testreporter.testEnd(executionId, nestingLevel, test, testName, result, executionTimeNanos);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void reportTestStart(String executionId, int nestingLevel, ITest test, String testName) {

		testreporter.testStart(executionId, nestingLevel, test, testName);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void sequenceEnd(String executionId, int nestingLevel, ITestSequence sequence,
		int okTests, int totalTests, long executionTimeNanos) {

		testreporter.sequenceEnd(executionId, nestingLevel, sequence, okTests, totalTests,
				executionTimeNanos);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void sequenceStart(String executionId, int nestingLevel, ITestSequence sequence) {

		testreporter.sequenceStart(executionId, nestingLevel, sequence);
	}
}
