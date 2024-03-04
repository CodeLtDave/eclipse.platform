/*******************************************************************************
 * Copyright (c) 2024 IBM Corporation and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.core.tests.filesystem.zip;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 *
 */
public class ExpandZipPerformanceTest {

	@Before
	public void setup() throws Exception {
		ZipFileSystemTestSetup.performanceSetup();
	}

	@After
	public void teardown() throws Exception {
		ZipFileSystemTestSetup.teardown();
	}

	@Test
	public void testPerformanceExpandSmallZip() throws Exception {
		ZipFileSystemTestUtil
				.collapseZipFile(ZipFileSystemTestSetup.project.getFolder(ZipFileSystemTestSetup.ZIP_FILE_VIRTUAL_FOLDER_NAME));

		long expectedMaxDuration = 100;
		long startTime = System.currentTimeMillis();

		ZipFileSystemTestUtil.expandZipFile(ZipFileSystemTestSetup.project
				.getFile(ZipFileSystemTestSetup.ZIP_FILE_VIRTUAL_FOLDER_NAME));

		long endTime = System.currentTimeMillis();
		long duration = endTime - startTime; // Calculate the duration
		assertTrue("Expand zip operation took longer than expected: " + duration + "Millis",
				duration <= expectedMaxDuration);
		assertFalse("Expand zip operation took: " + duration + "Millis", duration <= expectedMaxDuration);
	}

	@Test
	public void testPerformanceExpandZip() throws Exception {
		long expectedMaxDuration = 1000;
		long startTime = System.currentTimeMillis();

		ZipFileSystemTestUtil.expandZipFile(
				ZipFileSystemTestSetup.project.getFile(ZipFileSystemTestSetup.PERFORMANCE_ZIP_FILE_NAME));

		long endTime = System.currentTimeMillis();
		long duration = endTime - startTime; // Calculate the duration
		assertTrue("Expand zip operation took longer than expected: " + duration + "Millis",
				duration <= expectedMaxDuration);
		assertFalse("Expand zip operation took: " + duration + "Millis", duration <= expectedMaxDuration);
	}

	@Test
	public void testPerformanceExpandBigZip() throws Exception {
		long expectedMaxDuration = 10000;
		long startTime = System.currentTimeMillis();

		ZipFileSystemTestUtil.expandZipFile(ZipFileSystemTestSetup.project
				.getFile(ZipFileSystemTestSetup.BIG_PERFORMANCE_ZIP_FILE_NAME));

		long endTime = System.currentTimeMillis();
		long duration = endTime - startTime; // Calculate the duration
		assertTrue("Expand zip operation took longer than expected: " + duration + "Millis",
				duration <= expectedMaxDuration);
		assertFalse("Expand zip operation took: " + duration + "Millis", duration <= expectedMaxDuration);
	}

	@Test
	public void testPerformanceExpandLargeZip() throws Exception {
		long expectedMaxDuration = 100000;
		long startTime = System.currentTimeMillis();

		ZipFileSystemTestUtil.expandZipFile(
				ZipFileSystemTestSetup.project.getFile(ZipFileSystemTestSetup.LARGE_PERFORMANCE_ZIP_FILE_NAME));

		long endTime = System.currentTimeMillis();
		long duration = endTime - startTime; // Calculate the duration
		assertTrue("Expand zip operation took longer than expected: " + duration + "Millis",
				duration <= expectedMaxDuration);
		assertFalse("Expand zip operation took: " + duration + "Millis", duration <= expectedMaxDuration);
	}

	@Test
	public void testPerformanceExpandHugeZip() throws Exception {
		long expectedMaxDuration = 1000000;
		long startTime = System.currentTimeMillis();

		ZipFileSystemTestUtil.expandZipFile(
				ZipFileSystemTestSetup.project.getFile(ZipFileSystemTestSetup.HUGE_PERFORMANCE_ZIP_FILE_NAME));

		long endTime = System.currentTimeMillis();
		long duration = endTime - startTime; // Calculate the duration
		assertTrue("Expand zip operation took longer than expected: " + duration + "Millis",
				duration <= expectedMaxDuration);
		assertFalse("Expand zip operation took: " + duration + "Millis", duration <= expectedMaxDuration);
	}
}
