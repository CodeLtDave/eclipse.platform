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

import static org.mockito.Mockito.mock;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.examples.filesystem.CollapseZipHandler;
import org.eclipse.ui.examples.filesystem.ExpandZipHandler;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *
 */
public class CollapseTest {

	@Before
	public void setup() throws Exception {
		ZipFileSystemTestSetup.setup();
		expandZipFile(ZipFileSystemTestSetup.project.getFile(ZipFileSystemTestSetup.ZIP_FILE_VIRTUAL_FOLDER_NAME));
	}

	@After
	public void teardown() throws Exception {
		ZipFileSystemTestSetup.teardown();
	}

	private void expandZipFile(IFile file) throws Exception {
		ExpandZipHandler expandZipHandler = new ExpandZipHandler();
		Shell shell = mock(Shell.class);
		expandZipHandler.expandZip(file, shell);
		IFolder virtualFolder = ZipFileSystemTestSetup.project.getFolder(file.getName());
		Assert.assertTrue("ZIP file should exist before deletion", virtualFolder.exists());
	}

	private void collapseZipFile(IFolder folder) throws Exception {
		CollapseZipHandler collapseZipHandler = new CollapseZipHandler();
		Shell shell = mock(Shell.class);
		collapseZipHandler.collapseZip(folder, shell);
		IFile zipFile = ZipFileSystemTestSetup.project.getFile(folder.getName());
		Assert.assertTrue("ZIP file should exist before deletion", zipFile.exists());
	}

	@Test
	public void testCollapseZipFile() throws Exception {
		IFolder virtualFolder = ZipFileSystemTestSetup.project
				.getFolder(ZipFileSystemTestSetup.ZIP_FILE_VIRTUAL_FOLDER_NAME);
		Assert.assertTrue("Virtual folder should exist in the project", virtualFolder.exists());
		collapseZipFile(virtualFolder);
		IFile zipFile = ZipFileSystemTestSetup.project.getFile(ZipFileSystemTestSetup.ZIP_FILE_VIRTUAL_FOLDER_NAME);
		Assert.assertTrue("Virtual folder should not exist in the project", !virtualFolder.exists());
		Assert.assertTrue("ZipFile should exist in the project", zipFile.exists());
	}
}
