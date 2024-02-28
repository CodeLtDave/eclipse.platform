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

import static org.eclipse.core.tests.filesystem.zip.ZipFileSystemTestUtil.ensureDoesNotExist;
import static org.eclipse.core.tests.filesystem.zip.ZipFileSystemTestUtil.ensureExists;
import static org.eclipse.core.tests.filesystem.zip.ZipFileSystemTestUtil.getMonitor;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 *
 */
public class RenameTest {

	@Before
	public void setup() throws Exception {
		ZipFileSystemTestSetup.setup();
	}

	@After
	public void teardown() throws Exception {
		ZipFileSystemTestSetup.teardown();
	}

	@Test
	public void testRenameZipArchive() throws Exception {
		// IFolder is renamed by moving with the new path
		IFolder virtualFolder = ZipFileSystemTestSetup.project
				.getFolder(ZipFileSystemTestSetup.ZIP_FILE_VIRTUAL_FOLDER_NAME);
		IFolder virtualFolder2 = ZipFileSystemTestSetup.project
				.getFolder(ZipFileSystemTestSetup.ZIP_FILE_VIRTUAL_FOLDER_NAME + "Renamed");
		virtualFolder.move(virtualFolder2.getFullPath(), false, getMonitor());
		ensureExists(virtualFolder2);
		ensureDoesNotExist(virtualFolder);
	}

	@Test
	public void testRenameFileInsideOfZip() throws Exception {
		// IFolder is renamed by moving with the new path
		IFile textFile = ZipFileSystemTestSetup.project.getFile(
				ZipFileSystemTestSetup.ZIP_FILE_VIRTUAL_FOLDER_NAME + "/" + ZipFileSystemTestSetup.TEXT_FILE_NAME);
		IFile newTextFile = ZipFileSystemTestSetup.project.getFile(ZipFileSystemTestSetup.ZIP_FILE_VIRTUAL_FOLDER_NAME
				+ "/" + ZipFileSystemTestSetup.TEXT_FILE_NAME + "Renamed");
		textFile.move(newTextFile.getFullPath(), false, getMonitor());
		ensureExists(newTextFile);
		ensureDoesNotExist(textFile);
	}
}
