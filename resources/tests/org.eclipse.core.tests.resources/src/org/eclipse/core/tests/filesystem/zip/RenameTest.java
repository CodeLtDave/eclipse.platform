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

import java.util.Arrays;
import java.util.Collection;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 *
 */
@RunWith(Parameterized.class)
public class RenameTest {

	@Parameterized.Parameters
	public static Collection<String[]> archiveNames() {
		return Arrays.asList(new String[][] { { ZipFileSystemTestSetup.ZIP_FILE_VIRTUAL_FOLDER_NAME },
				{ ZipFileSystemTestSetup.JAR_FILE_VIRTUAL_FOLDER_NAME } });
	}

	private String archiveName;

	public RenameTest(String archiveName) {
		this.archiveName = archiveName;
	}

	@Before
	public void setup() throws Exception {
		ZipFileSystemTestSetup.setup();
	}

	@After
	public void teardown() throws Exception {
		ZipFileSystemTestSetup.teardown();
	}

	@Test
	public void testRenameArchive() throws Exception {
		// IFolder is renamed by moving with the new path
		IFolder virtualFolder = ZipFileSystemTestSetup.project
				.getFolder(archiveName);
		IFolder virtualFolder2 = ZipFileSystemTestSetup.project
				.getFolder(archiveName + "Renamed");
		virtualFolder.move(virtualFolder2.getFullPath(), false, getMonitor());
		ensureExists(virtualFolder2);
		ensureDoesNotExist(virtualFolder);
	}

	@Test
	public void testRenameFileInsideOfArchive() throws Exception {
		// IFolder is renamed by moving with the new path
		IFile textFile = ZipFileSystemTestSetup.project.getFile(
				archiveName + "/" + ZipFileSystemTestSetup.TEXT_FILE_NAME);
		IFile newTextFile = ZipFileSystemTestSetup.project
				.getFile(archiveName
				+ "/" + ZipFileSystemTestSetup.TEXT_FILE_NAME + "Renamed");
		textFile.move(newTextFile.getFullPath(), false, getMonitor());
		ensureExists(newTextFile);
		ensureDoesNotExist(textFile);
	}
}
