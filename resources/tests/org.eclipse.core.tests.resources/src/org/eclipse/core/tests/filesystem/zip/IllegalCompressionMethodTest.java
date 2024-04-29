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
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.URISyntaxException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
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
		try {
			ZipFileSystemTestUtil.expandArchive(archiveFile);
		} catch (CoreException e) {
			ensureExists(archiveFile);
			assertEquals("Archive could not be expanded or has no children", e.getMessage());
		}
	}
}

