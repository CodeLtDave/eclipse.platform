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
import static org.junit.Assert.assertFalse;

import java.io.IOException;
import java.net.URISyntaxException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ArchiveTransformer;
import org.eclipse.core.runtime.CoreException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 *
 */
public class IllegalCompressionMethodTest {

	private static final String ARCHIVE_NAME = "EnhancedDeflated.zip";

	@Before
	public void setup() throws Exception {
		ZipFileSystemTestSetup.setup();
		ZipFileSystemTestSetup.copyArchiveIntoJavaProject(ZipFileSystemTestSetup.firstProject, ARCHIVE_NAME);
	}

	@After
	public void teardown() throws Exception {
		ZipFileSystemTestSetup.teardown();
	}

	@Test
	public void testExpandEnhancedDeflatedArchive() throws CoreException, URISyntaxException, IOException {
		IProject project = ZipFileSystemTestSetup.firstProject;
		IFile archiveFile = project.getFile(ARCHIVE_NAME);

		ensureExists(archiveFile);
		ArchiveTransformer.expandArchive(archiveFile);

		ensureExists(archiveFile);
		IFolder archiveFolder = project.getFolder(ARCHIVE_NAME);
		assertFalse(
				"Error: A folder named '\" + ARCHIVE_NAME + \"' exists which should not. The zip should have been collapsed against\"",
				archiveFolder.exists());
	}
}

