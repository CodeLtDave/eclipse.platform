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
public class CollapseTest {

	@Parameterized.Parameters
	public static Collection<String[]> archiveNames() {
		return Arrays.asList(new String[][] { { ZipFileSystemTestSetup.ZIP_FILE_VIRTUAL_FOLDER_NAME },
				{ ZipFileSystemTestSetup.JAR_FILE_VIRTUAL_FOLDER_NAME } });
	}

	private String archiveName;

	public CollapseTest(String archiveName) {
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
	public void testCollapseArchive() throws Exception {
		IFolder virtualFolder = ZipFileSystemTestSetup.project
				.getFolder(archiveName);
		ensureExists(virtualFolder);
		ZipFileSystemTestUtil.collapseZipFile(virtualFolder);
		IFile zipFile = ZipFileSystemTestSetup.project.getFile(archiveName);
		// Don't use Utility method ensureDoesNotExist because the fileStore is still
		// available after collapse. The fileStore is the File itself in the local file
		// system that still exists after collapse.
		assertTrue("folder was not properly deleted: " + virtualFolder, !virtualFolder.exists());
		ensureExists(zipFile);
	}
}
