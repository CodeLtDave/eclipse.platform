/*******************************************************************************
 * Copyright (c) 2024 Vector Informatik GmbH and others.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors: Vector Informatik GmbH - initial API and implementation
 *******************************************************************************/

package org.eclipse.core.tests.filesystem.zip;

import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class OpenZipPerformanceTest {

	@BeforeEach
	public void setup() throws Exception {
		ZipFileSystemTestSetup.performanceSetup();
	}

	@AfterEach
	public void teardown() throws Exception {
		ZipFileSystemTestSetup.teardown();
	}

	@Test
	public void testPerformanceOpenSmallZip() throws Exception {
		ZipFileSystemTestUtil
				.closeZipFile(ZipFileSystemTestSetup.firstProject
						.getFolder(ZipFileSystemTestSetup.ZIP_FILE_VIRTUAL_FOLDER_NAME));

		long expectedMaxDuration = 100;
		long startTime = System.currentTimeMillis();

		ZipFileSystemTestUtil.openZipFile(ZipFileSystemTestSetup.firstProject
				.getFile(ZipFileSystemTestSetup.ZIP_FILE_VIRTUAL_FOLDER_NAME));

		long endTime = System.currentTimeMillis();
		long duration = endTime - startTime; // Calculate the duration
		assertTrue("Open zip operation took longer than expected: " + duration + "Millis",
				duration <= expectedMaxDuration);
		printDuration(duration, ZipFileSystemTestSetup.ZIP_FILE_VIRTUAL_FOLDER_NAME);
	}

	@Test
	public void testPerformanceOpenZip() throws Exception {
		long expectedMaxDuration = 100;
		long startTime = System.currentTimeMillis();

		ZipFileSystemTestUtil.openZipFile(
				ZipFileSystemTestSetup.firstProject.getFile(ZipFileSystemTestSetup.PERFORMANCE_ZIP_FILE_NAME));

		long endTime = System.currentTimeMillis();
		long duration = endTime - startTime; // Calculate the duration
		assertTrue("Open zip operation took longer than expected: " + duration + "Millis",
				duration <= expectedMaxDuration);
		printDuration(duration, ZipFileSystemTestSetup.PERFORMANCE_ZIP_FILE_NAME);
	}

	@Test
	public void testPerformanceOpenBigZip() throws Exception {
		long expectedMaxDuration = 1000;
		long startTime = System.currentTimeMillis();

		ZipFileSystemTestUtil.openZipFile(ZipFileSystemTestSetup.firstProject
				.getFile(ZipFileSystemTestSetup.BIG_PERFORMANCE_ZIP_FILE_NAME));

		long endTime = System.currentTimeMillis();
		long duration = endTime - startTime; // Calculate the duration
		assertTrue("Open zip operation took longer than expected: " + duration + "Millis",
				duration <= expectedMaxDuration);
		printDuration(duration, ZipFileSystemTestSetup.BIG_PERFORMANCE_ZIP_FILE_NAME);
	}

	@Test
	public void testPerformanceOpenLargeZip() throws Exception {
		long expectedMaxDuration = 10000;
		long startTime = System.currentTimeMillis();

		ZipFileSystemTestUtil.openZipFile(
				ZipFileSystemTestSetup.firstProject.getFile(ZipFileSystemTestSetup.LARGE_PERFORMANCE_ZIP_FILE_NAME));

		long endTime = System.currentTimeMillis();
		long duration = endTime - startTime; // Calculate the duration
		assertTrue("Open zip operation took longer than expected: " + duration + "Millis",
				duration <= expectedMaxDuration);
		printDuration(duration, ZipFileSystemTestSetup.LARGE_PERFORMANCE_ZIP_FILE_NAME);
	}

	@Test
	public void testPerformanceOpenHugeZip() throws Exception {
		long expectedMaxDuration = 20000;
		long startTime = System.currentTimeMillis();

		ZipFileSystemTestUtil.openZipFile(
				ZipFileSystemTestSetup.firstProject.getFile(ZipFileSystemTestSetup.HUGE_PERFORMANCE_ZIP_FILE_NAME));

		long endTime = System.currentTimeMillis();
		long duration = endTime - startTime; // Calculate the duration
		assertTrue("Open zip operation took longer than expected: " + duration + "Millis",
				duration <= expectedMaxDuration);
		printDuration(duration, ZipFileSystemTestSetup.HUGE_PERFORMANCE_ZIP_FILE_NAME);
	}

	public void printDuration(long duration, String zipFile) {
		System.out.println("Open zip operation for " + zipFile + " took: " + duration + "ms");
	}
}
