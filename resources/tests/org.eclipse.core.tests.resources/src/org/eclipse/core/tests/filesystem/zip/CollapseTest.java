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

import static org.eclipse.core.tests.filesystem.zip.ZipFileSystemTestUtil.ensureExists;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.internal.ide.commands.CollapseZipHandler;
import org.eclipse.ui.internal.ide.commands.ExpandZipHandler;
import org.junit.After;
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
		ensureExists(virtualFolder);
	}

	private void collapseZipFile(IFolder folder) throws Exception {
		CollapseZipHandler collapseZipHandler = new CollapseZipHandler();
		Shell shell = mock(Shell.class);
		collapseZipHandler.collapseZip(folder, shell);
		IFile zipFile = ZipFileSystemTestSetup.project.getFile(folder.getName());
		ensureExists(zipFile);
	}

	@Test
	public void testCollapseZipFile() throws Exception {
		IFolder virtualFolder = ZipFileSystemTestSetup.project
				.getFolder(ZipFileSystemTestSetup.ZIP_FILE_VIRTUAL_FOLDER_NAME);
		ensureExists(virtualFolder);
		collapseZipFile(virtualFolder);
		IFile zipFile = ZipFileSystemTestSetup.project.getFile(ZipFileSystemTestSetup.ZIP_FILE_VIRTUAL_FOLDER_NAME);
		// Don't use Utility method ensureDoesNotExist because the fileStore is still
		// available after collapse. The fileStore is the File itself in the local file
		// system that still exists after collapse.
		assertTrue("folder was not properly deleted: " + virtualFolder, !virtualFolder.exists());
		ensureExists(zipFile);
	}
}
