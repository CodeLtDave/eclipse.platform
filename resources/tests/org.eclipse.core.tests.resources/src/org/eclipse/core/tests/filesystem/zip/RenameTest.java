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

import static org.eclipse.core.tests.filesystem.zip.ZipFileSystemTestUtil.ensureDoesNotExist;
import static org.eclipse.core.tests.filesystem.zip.ZipFileSystemTestUtil.ensureExists;
import static org.eclipse.core.tests.filesystem.zip.ZipFileSystemTestUtil.getMonitor;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class RenameTest {

	@BeforeEach
	public void setup() throws Exception {
		ZipFileSystemTestSetup.defaultSetup();
	}

	@AfterEach
	public void teardown() throws Exception {
		ZipFileSystemTestSetup.teardown();
	}

	@ParameterizedTest
	@MethodSource("org.eclipse.core.tests.filesystem.zip.ZipFileSystemTestUtil#zipFileNames")
	public void testRenameZipFile(String zipFileName) throws Exception {
		// IFolder is renamed by moving with the new path
		IFolder openedZipFile = ZipFileSystemTestSetup.firstProject.getFolder(zipFileName);
		IFolder openedZipFileWithNewName = ZipFileSystemTestSetup.firstProject.getFolder(zipFileName + "Renamed");
		openedZipFile.move(openedZipFileWithNewName.getFullPath(), false, getMonitor());
		ensureExists(openedZipFileWithNewName);
		ensureDoesNotExist(openedZipFile);
	}

	@ParameterizedTest
	@MethodSource("org.eclipse.core.tests.filesystem.zip.ZipFileSystemTestUtil#zipFileNames")
	public void testRenameFileInsideOfZipFile(String zipFileName) throws Exception {
		// IFolder is renamed by moving with the new path
		IFile textFile = ZipFileSystemTestSetup.firstProject.getFile(
				zipFileName + "/" + ZipFileSystemTestSetup.TEXT_FILE_NAME);
		IFile newTextFile = ZipFileSystemTestSetup.firstProject
				.getFile(zipFileName
				+ "/" + ZipFileSystemTestSetup.TEXT_FILE_NAME + "Renamed");
		textFile.move(newTextFile.getFullPath(), false, getMonitor());
		ensureExists(newTextFile);
		ensureDoesNotExist(textFile);
	}
}
