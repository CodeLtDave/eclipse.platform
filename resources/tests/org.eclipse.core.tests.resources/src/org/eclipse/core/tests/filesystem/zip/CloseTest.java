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

import static org.eclipse.core.tests.filesystem.zip.ZipFileSystemTestUtil.ensureExists;
import static org.junit.Assert.assertTrue;

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
public class CloseTest {

	@Parameterized.Parameters
	public static Collection<String[]> zipFileNames() {
		return Arrays.asList(new String[][] { { ZipFileSystemTestSetup.ZIP_FILE_VIRTUAL_FOLDER_NAME } });
	}

	private String zipFileName;

	public CloseTest(String zipFileName) {
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
	public void testCloseZipFile() throws Exception {
		IFolder openedZipFile = ZipFileSystemTestSetup.firstProject
				.getFolder(zipFileName);
		ensureExists(openedZipFile);
		ZipFileSystemTestUtil.closeZipFile(openedZipFile);
		IFile zipFile = ZipFileSystemTestSetup.firstProject.getFile(zipFileName);
		// Don't use Utility method ensureDoesNotExist because the fileStore is still
		// available after closing. The fileStore is the File itself in the local file
		// system that still exists after closing.
		assertTrue("folder was not properly deleted: " + openedZipFile, !openedZipFile.exists());
		ensureExists(zipFile);
	}

	@Test
	public void testCloseNestedZipFileParentWhenChildIsOpened() throws Exception {
		ZipFileSystemTestSetup.copyAndOpenNestedZipFileIntoJavaProject();
		IFile nestedZipFileParent = ZipFileSystemTestSetup.firstProject
				.getFile(ZipFileSystemTestSetup.NESTED_ZIP_FILE_PARENT_NAME);
		IFolder openedNestedZipFileParent = ZipFileSystemTestSetup.firstProject
				.getFolder(ZipFileSystemTestSetup.NESTED_ZIP_FILE_PARENT_NAME);
		ZipFileSystemTestUtil.closeZipFile(openedNestedZipFileParent);
		assertTrue("folder was not properly deleted: " + openedNestedZipFileParent,
				!openedNestedZipFileParent.exists());
		ensureExists(nestedZipFileParent);
	}

	@Test
	public void testCloseNestedZipFileChild() throws Exception {
		ZipFileSystemTestSetup.copyAndOpenNestedZipFileIntoJavaProject();
		IFolder openedNestedZipFileParent = ZipFileSystemTestSetup.firstProject
				.getFolder(ZipFileSystemTestSetup.NESTED_ZIP_FILE_PARENT_NAME);
		IFile nestedZipFileChild = openedNestedZipFileParent.getFile(ZipFileSystemTestSetup.NESTED_ZIP_FILE_CHILD_NAME);
		IFolder openedNestedZipFileChild = openedNestedZipFileParent
				.getFolder(ZipFileSystemTestSetup.NESTED_ZIP_FILE_CHILD_NAME);
		ZipFileSystemTestUtil.closeZipFile(openedNestedZipFileChild);
		assertTrue("folder was not properly deleted: " + openedNestedZipFileChild, !openedNestedZipFileChild.exists());
		ensureExists(nestedZipFileChild);
		ensureExists(openedNestedZipFileParent);
	}

}
