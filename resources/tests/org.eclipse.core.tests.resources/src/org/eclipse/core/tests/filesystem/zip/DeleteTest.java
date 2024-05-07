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

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collection;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 *
 */
@RunWith(Parameterized.class)
public class DeleteTest {

	@Parameterized.Parameters
	public static Collection<String[]> zipFileNames() {
		return Arrays.asList(new String[][] { { ZipFileSystemTestSetup.ZIP_FILE_VIRTUAL_FOLDER_NAME },
				{ ZipFileSystemTestSetup.JAR_FILE_VIRTUAL_FOLDER_NAME } });
	}

	private String zipFileName;

	public DeleteTest(String zipFileName) {
		this.zipFileName = zipFileName;
	}

	@Before
	public void setup() throws Exception {
		ZipFileSystemTestSetup.defaultSetup();
	}

	@After
	public void teardown() throws Exception {
		ZipFileSystemTestSetup.teardown();
	}

	@Test
	public void testDeleteZipFile() throws CoreException, IOException {
		IFolder openedZipFile = ZipFileSystemTestSetup.firstProject
				.getFolder(zipFileName);
		ensureExists(openedZipFile);
		openedZipFile.delete(false, false, getMonitor());
		ensureDoesNotExist(openedZipFile);
		IFile zipFile = ZipFileSystemTestSetup.firstProject.getFile(zipFileName);
		ensureDoesNotExist(zipFile);
	}

	@Test
	public void testDeleteFileInsideOfZipFile() throws CoreException, IOException {
		IFolder openedZipFile = ZipFileSystemTestSetup.firstProject
				.getFolder(zipFileName);
		IFile textFile = openedZipFile.getFile(ZipFileSystemTestSetup.TEXT_FILE_NAME);
		ensureExists(textFile);
		textFile.delete(true, getMonitor());
		ensureDoesNotExist(textFile);
	}

	@Test
	public void testDeleteNestedZipFileParent() throws CoreException, IOException, URISyntaxException {
		ZipFileSystemTestSetup.copyAndOpenNestedZipFileIntoJavaProject();
		IFile nestedZipFileParent = ZipFileSystemTestSetup.firstProject
				.getFile(ZipFileSystemTestSetup.NESTED_ZIP_FILE_PARENT_NAME);
		IFolder openedNestedZipFileParent = ZipFileSystemTestSetup.firstProject
				.getFolder(ZipFileSystemTestSetup.NESTED_ZIP_FILE_PARENT_NAME);
		openedNestedZipFileParent.delete(true, getMonitor());
		ensureDoesNotExist(openedNestedZipFileParent);
		ensureDoesNotExist(nestedZipFileParent);
	}

	@Test
	public void testDeleteNestedZipFileChild() throws CoreException, IOException, URISyntaxException {
		ZipFileSystemTestSetup.copyAndOpenNestedZipFileIntoJavaProject();
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
