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

import static org.eclipse.core.tests.filesystem.zip.ZipFileSystemTestUtil.ensureDoesNotExist;
import static org.eclipse.core.tests.filesystem.zip.ZipFileSystemTestUtil.ensureExists;
import static org.eclipse.core.tests.filesystem.zip.ZipFileSystemTestUtil.getMonitor;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collection;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.ArchiveTransformer;
import org.eclipse.core.runtime.CoreException;
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
public class MoveTest {

	@Parameterized.Parameters
	public static Collection<String[]> archiveNames() {
		return Arrays.asList(new String[][] { { ZipFileSystemTestSetup.ZIP_FILE_VIRTUAL_FOLDER_NAME },
				{ ZipFileSystemTestSetup.JAR_FILE_VIRTUAL_FOLDER_NAME } });
	}

	private String archiveName;

	public MoveTest(String archiveName) {
		this.archiveName = archiveName;
	}

	@Before
	public void setup() throws Exception {
		ZipFileSystemTestSetup.setupWithTwoProjects();
	}

	@After
	public void teardown() throws Exception {
		ZipFileSystemTestSetup.teardown();
	}

	@Test
	public void testMoveArchiveWithinProject() throws CoreException, IOException {
		IFolder archiveFolder = ZipFileSystemTestSetup.firstProject.getFolder(archiveName);
		IFolder destinationFolder = ZipFileSystemTestSetup.firstProject.getFolder("destinationFolder");
		destinationFolder.create(false, true, getMonitor());
		IFolder destination = ZipFileSystemTestSetup.firstProject
				.getFolder("destinationFolder/" + archiveName);

		archiveFolder.move(destination.getFullPath(), false, getMonitor());

		// Verify that the folder exists at the new location and not at the old location
		// anymore
		IFolder newFolder = ZipFileSystemTestSetup.firstProject
				.getFolder(destinationFolder.getName() + "/" + archiveName);
		ensureExists(newFolder);
		ensureDoesNotExist(archiveFolder);
	}

	@Test
	public void testMoveArchiveToOtherProject() throws CoreException, IOException {
		IFolder archiveFolder = ZipFileSystemTestSetup.firstProject.getFolder(archiveName);
		IFolder destination = ZipFileSystemTestSetup.secondProject.getFolder(archiveName);
		archiveFolder.move(destination.getFullPath(), false, getMonitor());

		// Verify that the folder exists at the new location and not at the old location
		// anymore
		IFolder newFolder = ZipFileSystemTestSetup.secondProject.getFolder(archiveName);
		ensureExists(newFolder);
		ensureDoesNotExist(archiveFolder);
	}

	@Test
	public void testMoveArchiveToOtherProjectFolder() throws CoreException, IOException {
		IFolder archiveFolder = ZipFileSystemTestSetup.firstProject.getFolder(archiveName);
		IFolder destinationFolder = ZipFileSystemTestSetup.secondProject.getFolder("destinationFolder");
		destinationFolder.create(false, true, getMonitor());
		IFolder destination = ZipFileSystemTestSetup.secondProject
				.getFolder("destinationFolder/" + archiveName);
		archiveFolder.move(destination.getFullPath(), false, getMonitor());

		// Verify that the folder exists at the new location and not at the old location
		// anymore
		IFolder newFolder = ZipFileSystemTestSetup.secondProject
				.getFolder(destinationFolder.getName() + "/" + archiveName);
		ensureExists(newFolder);
		ensureDoesNotExist(archiveFolder);
	}

	@Test
	public void testMoveFileIntoArchive() throws Exception {
		IFile textFile = ZipFileSystemTestSetup.firstProject.getFile("NewFile.txt");
		ensureDoesNotExist(textFile);
		String text = "Foo";
		InputStream stream = new ByteArrayInputStream(text.getBytes());
		textFile.create(stream, false, getMonitor());
		stream.close();
		ensureExists(textFile);
		IFile destinationFile = ZipFileSystemTestSetup.firstProject
				.getFile(archiveName + "/" + "NewFile.txt");
		textFile.move(destinationFile.getFullPath(), false, getMonitor());

		// Verify that the file exists at the new location
		ensureExists(destinationFile);
		try (InputStreamReader isr = new InputStreamReader(destinationFile.getContents());
				BufferedReader reader = new BufferedReader(isr)) {
			String content = reader.readLine();
			Assert.assertEquals("The content of NewFile.txt should be 'Foo'", "Foo", content);
		}

		// Verify that the file does not exist at the old location
		ensureDoesNotExist(textFile);
	}

	@Test
	public void testMoveFileFromArchive() throws Exception {
		IFile textFile = ZipFileSystemTestSetup.firstProject.getFile(
				archiveName + "/" + ZipFileSystemTestSetup.TEXT_FILE_NAME);
		ensureExists(textFile);
		IFile destinationFile = ZipFileSystemTestSetup.firstProject.getFile(ZipFileSystemTestSetup.TEXT_FILE_NAME);
		textFile.move(destinationFile.getFullPath(), false, getMonitor());

		// Verify that the file exists at the new location
		ensureExists(destinationFile);
		try (InputStreamReader isr = new InputStreamReader(destinationFile.getContents());
				BufferedReader reader = new BufferedReader(isr)) {
			String content = reader.readLine();
			Assert.assertEquals("The content of " + ZipFileSystemTestSetup.TEXT_FILE_NAME + " should be 'Hello World!'",
					"Hello World!",
					content);
		}

		// Verify that the file does not exist at the old location
		ensureDoesNotExist(textFile);
	}

	@Test
	public void testMoveFileInsideOfArchive() throws Exception {
		IFolder archiveFolder = ZipFileSystemTestSetup.firstProject.getFolder(archiveName);
		IFolder destinationFolder = archiveFolder.getFolder("destinationFolder");
		ensureDoesNotExist(destinationFolder);
		destinationFolder.create(false, true, getMonitor());
		ensureExists(destinationFolder);
		IFile textFile = archiveFolder.getFile(ZipFileSystemTestSetup.TEXT_FILE_NAME);
		ensureExists(textFile);
		IFile fileDestination = destinationFolder.getFile(ZipFileSystemTestSetup.TEXT_FILE_NAME);
		ensureDoesNotExist(fileDestination);
		textFile.move(fileDestination.getFullPath(), false, getMonitor());
		ensureExists(fileDestination);
		ensureDoesNotExist(textFile);
	}

	@Test
	public void testMoveArchiveIntoArchive() throws Exception {
		IFolder archiveFolder = ZipFileSystemTestSetup.firstProject.getFolder(archiveName);
		// create and expand second archive
		String newArchiveName = archiveName.replace(".", "New.");
		IFile newArchiveFile = ZipFileSystemTestSetup.firstProject.getFile(newArchiveName);
		ensureDoesNotExist(newArchiveFile);
		ZipFileSystemTestSetup.copyArchiveIntoJavaProject(ZipFileSystemTestSetup.firstProject, newArchiveName);
		ensureExists(newArchiveFile);
		ArchiveTransformer.expandArchive(newArchiveFile);
		IFolder newArchiveFolder = ZipFileSystemTestSetup.firstProject.getFolder(newArchiveName);
		ensureExists(newArchiveFolder);
		// move second archive into first archive
		IFolder newArchiveFolderDestination = archiveFolder.getFolder(newArchiveName);
		newArchiveFolder.move(newArchiveFolderDestination.getFullPath(), false, getMonitor());
		ensureExists(newArchiveFolderDestination);
		ensureDoesNotExist(newArchiveFolder);
	}
}
