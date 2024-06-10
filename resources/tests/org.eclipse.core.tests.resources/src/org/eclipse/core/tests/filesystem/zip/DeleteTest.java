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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class DeleteTest {

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
	public void testDeleteZipFile(String zipFileName) throws CoreException, IOException {
		IFolder openedZipFile = ZipFileSystemTestSetup.firstProject
				.getFolder(zipFileName);
		ensureExists(openedZipFile);
		openedZipFile.delete(false, false, getMonitor());
		ensureDoesNotExist(openedZipFile);
		IFile zipFile = ZipFileSystemTestSetup.firstProject.getFile(zipFileName);
		ensureDoesNotExist(zipFile);
	}

	@ParameterizedTest
	@MethodSource("org.eclipse.core.tests.filesystem.zip.ZipFileSystemTestUtil#zipFileNames")
	public void testDeleteFileInsideOfZipFile(String zipFileName) throws CoreException, IOException {
		IFolder openedZipFile = ZipFileSystemTestSetup.firstProject
				.getFolder(zipFileName);
		IFile textFile = openedZipFile.getFile(ZipFileSystemTestSetup.TEXT_FILE_NAME);
		ensureExists(textFile);
		textFile.delete(true, getMonitor());
		ensureDoesNotExist(textFile);
	}

	@ParameterizedTest
	@MethodSource("org.eclipse.core.tests.filesystem.zip.ZipFileSystemTestUtil#zipFileNames")
	public void testDeleteEmptyFolder(String zipFileName) throws CoreException, IOException {
		IFolder openedZipFile = ZipFileSystemTestSetup.firstProject.getFolder(zipFileName);
		IFolder folder = openedZipFile.getFolder("FolderToDelete");
		ensureDoesNotExist(folder);
		folder.create(true, true, getMonitor());
		ensureExists(folder);
		folder.delete(true, getMonitor());
		ensureDoesNotExist(folder);
	}


	@ParameterizedTest
	@MethodSource("org.eclipse.core.tests.filesystem.zip.ZipFileSystemTestUtil#zipFileNames")
	public void testDeleteFolderWithChildren(String zipFileName) throws CoreException, IOException {
		IFolder openedZipFile = ZipFileSystemTestSetup.firstProject.getFolder(zipFileName);
		IFolder folder = openedZipFile.getFolder("FolderToDelete");
		ensureDoesNotExist(folder);
		folder.create(true, true, getMonitor());
		ensureExists(folder);
		IFile textFile = folder.getFile(ZipFileSystemTestSetup.TEXT_FILE_NAME);
		textFile.create(new ByteArrayInputStream("Hello World!".getBytes()), true, getMonitor());
		ensureExists(textFile);
		folder.delete(true, getMonitor());
		ensureDoesNotExist(folder);
		ensureDoesNotExist(textFile);
	}


	@ParameterizedTest
	@MethodSource("org.eclipse.core.tests.filesystem.zip.ZipFileSystemTestUtil#zipFileNames")
	public void testDeleteNestedZipFileParent(String zipFileName)
			throws CoreException, IOException, URISyntaxException {
		ZipFileSystemTestSetup.copyAndOpenNestedZipFileIntoProject();
		IFile nestedZipFileParent = ZipFileSystemTestSetup.firstProject
				.getFile(ZipFileSystemTestSetup.NESTED_ZIP_FILE_PARENT_NAME);
		IFolder openedNestedZipFileParent = ZipFileSystemTestSetup.firstProject
				.getFolder(ZipFileSystemTestSetup.NESTED_ZIP_FILE_PARENT_NAME);
		openedNestedZipFileParent.delete(true, getMonitor());
		ensureDoesNotExist(openedNestedZipFileParent);
		ensureDoesNotExist(nestedZipFileParent);
	}

	@ParameterizedTest
	@MethodSource("org.eclipse.core.tests.filesystem.zip.ZipFileSystemTestUtil#zipFileNames")
	public void testDeleteNestedZipFileChild(String zipFileName) throws CoreException, IOException, URISyntaxException {
		ZipFileSystemTestSetup.copyAndOpenNestedZipFileIntoProject();
		IFolder openedNestedZipFileParent = ZipFileSystemTestSetup.firstProject
				.getFolder(ZipFileSystemTestSetup.NESTED_ZIP_FILE_PARENT_NAME);
		IFile nestedZipFileChild = openedNestedZipFileParent.getFile(ZipFileSystemTestSetup.NESTED_ZIP_FILE_CHILD_NAME);
		IFolder openedNestedZipFileChild = openedNestedZipFileParent
				.getFolder(ZipFileSystemTestSetup.NESTED_ZIP_FILE_CHILD_NAME);
		openedNestedZipFileChild.delete(true, getMonitor());
		ensureDoesNotExist(openedNestedZipFileChild);
		ensureDoesNotExist(nestedZipFileChild);
		ensureExists(openedNestedZipFileParent);
	}
}
