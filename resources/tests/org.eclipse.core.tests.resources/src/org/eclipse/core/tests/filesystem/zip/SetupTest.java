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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collection;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 *
 */
@RunWith(Parameterized.class)
public class SetupTest {

	@Parameterized.Parameters
	public static Collection<String[]> archiveNames() {
		return Arrays.asList(new String[][] { { ZipFileSystemTestSetup.ZIP_FILE_VIRTUAL_FOLDER_NAME },
				{ ZipFileSystemTestSetup.JAR_FILE_VIRTUAL_FOLDER_NAME } });
	}

	private String archiveName;

	public SetupTest(String archiveName) {
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
	public void testArchiveInProject() throws Exception {
		// Check if the "virtual folder" (ZIP file) exists in the project
		IFolder virtualFolder = ZipFileSystemTestSetup.project
				.getFolder(archiveName);
		ensureExists(virtualFolder);
	}

	@Test
	public void testTextFileInArchive() throws Exception {
		IFolder virtualFolder = ZipFileSystemTestSetup.project
				.getFolder(archiveName);

		IFile textFile = virtualFolder.getFile(ZipFileSystemTestSetup.TEXT_FILE_NAME);
		ensureExists(textFile);

		// Read and verify the content of Text.txt
		try (InputStreamReader isr = new InputStreamReader(textFile.getContents());
				BufferedReader reader = new BufferedReader(isr)) {
			String content = reader.readLine(); // Assuming the file has a single line with "Hello World!"
			Assert.assertEquals("The content of Text.txt should be 'Hello World!'", "Hello World!", content);
		}
	}
}
