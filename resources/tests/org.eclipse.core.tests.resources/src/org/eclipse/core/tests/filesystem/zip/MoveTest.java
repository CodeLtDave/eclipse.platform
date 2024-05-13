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

import static org.eclipse.core.tests.filesystem.zip.ZipFileSystemTestUtil.assertTextFileContent;
import static org.eclipse.core.tests.filesystem.zip.ZipFileSystemTestUtil.ensureDoesNotExist;
import static org.eclipse.core.tests.filesystem.zip.ZipFileSystemTestUtil.ensureExists;
import static org.eclipse.core.tests.filesystem.zip.ZipFileSystemTestUtil.getMonitor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
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
public class MoveTest {

	@Parameterized.Parameters
	public static Collection<String[]> zipFileNames() {
		return Arrays.asList(new String[][] { { ZipFileSystemTestSetup.ZIP_FILE_VIRTUAL_FOLDER_NAME },
				{ ZipFileSystemTestSetup.JAR_FILE_VIRTUAL_FOLDER_NAME } });
	}

	private String zipFileName;

	public MoveTest(String zipFileName) {
		this.zipFileName = zipFileName;
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
	public void testMoveZipFileWithinProject() throws CoreException, IOException {
		IFolder openedZipFile = ZipFileSystemTestSetup.firstProject.getFolder(zipFileName);
		IFolder destinationFolder = ZipFileSystemTestSetup.firstProject.getFolder("destinationFolder");
		destinationFolder.create(false, true, getMonitor());
		IFolder destination = ZipFileSystemTestSetup.firstProject
				.getFolder("destinationFolder/" + zipFileName);

		openedZipFile.move(destination.getFullPath(), false, getMonitor());

		// Verify that the folder exists at the new location and not at the old location
		// anymore
		IFolder newFolder = ZipFileSystemTestSetup.firstProject
				.getFolder(destinationFolder.getName() + "/" + zipFileName);
		ensureExists(newFolder);
		ensureDoesNotExist(openedZipFile);
	}

	@Test
	public void testMoveZipFileToOtherProject() throws CoreException, IOException {
		IFolder openedZipFile = ZipFileSystemTestSetup.firstProject.getFolder(zipFileName);
		IFolder destination = ZipFileSystemTestSetup.secondProject.getFolder(zipFileName);
		openedZipFile.move(destination.getFullPath(), false, getMonitor());

		// Verify that the folder exists at the new location and not at the old location
		// anymore
		IFolder newFolder = ZipFileSystemTestSetup.secondProject.getFolder(zipFileName);
		ensureExists(newFolder);
		ensureDoesNotExist(openedZipFile);
	}

	@Test
	public void testMoveZipFileToOtherProjectFolder() throws CoreException, IOException {
		IFolder openedZipFile = ZipFileSystemTestSetup.firstProject.getFolder(zipFileName);
		IFolder destinationFolder = ZipFileSystemTestSetup.secondProject.getFolder("destinationFolder");
		destinationFolder.create(false, true, getMonitor());
		IFolder destination = ZipFileSystemTestSetup.secondProject
				.getFolder("destinationFolder/" + zipFileName);
		openedZipFile.move(destination.getFullPath(), false, getMonitor());

		// Verify that the folder exists at the new location and not at the old location
		// anymore
		IFolder newFolder = ZipFileSystemTestSetup.secondProject
				.getFolder(destinationFolder.getName() + "/" + zipFileName);
		ensureExists(newFolder);
		ensureDoesNotExist(openedZipFile);
	}

	@Test
	public void testMoveFileIntoZipFile() throws Exception {
		IFile textFile = ZipFileSystemTestSetup.firstProject.getFile("NewFile.txt");
		ensureDoesNotExist(textFile);
		String text = "Foo";
		InputStream stream = new ByteArrayInputStream(text.getBytes());
		textFile.create(stream, false, getMonitor());
		stream.close();
		ensureExists(textFile);
		IFile destinationFile = ZipFileSystemTestSetup.firstProject
				.getFile(zipFileName + "/" + "NewFile.txt");
		textFile.move(destinationFile.getFullPath(), false, getMonitor());

		// Verify that the file exists at the new location
		ensureExists(destinationFile);
		assertTextFileContent(destinationFile, text);

		// Verify that the file does not exist at the old location
		ensureDoesNotExist(textFile);
	}

	@Test
	public void testMoveFolderIntoZipFile() throws Exception {
		IFolder openedZipFile = ZipFileSystemTestSetup.firstProject.getFolder(zipFileName);
		IFolder destinationFolder = openedZipFile.getFolder("destinationFolder");
		ensureDoesNotExist(destinationFolder);
		destinationFolder.create(false, true, getMonitor());
		ensureExists(destinationFolder);
		IFolder newFolder = ZipFileSystemTestSetup.firstProject.getFolder("NewFolder");
		ensureDoesNotExist(newFolder);
		newFolder.create(false, true, getMonitor());
		ensureExists(newFolder);
		IFolder newFolderDestination = destinationFolder.getFolder("NewFolder");
		newFolder.move(newFolderDestination.getFullPath(), false, getMonitor());
		ensureDoesNotExist(newFolder);
		ensureExists(newFolderDestination);
	}

	@Test
	public void testMoveFolderWithContentIntoZipFile() throws Exception {
		IFolder openedZipFile = ZipFileSystemTestSetup.firstProject.getFolder(zipFileName);
		IFolder destinationFolder = openedZipFile.getFolder("destinationFolder");
		ensureDoesNotExist(destinationFolder);
		destinationFolder.create(false, true, getMonitor());
		ensureExists(destinationFolder);
		IFolder newFolder = ZipFileSystemTestSetup.firstProject.getFolder("NewFolder");
		ensureDoesNotExist(newFolder);
		newFolder.create(false, true, getMonitor());
		ensureExists(newFolder);
		IFile textFile = newFolder.getFile("NewFile.txt");
		ensureDoesNotExist(textFile);
		String text = "Foo";
		InputStream stream = new ByteArrayInputStream(text.getBytes());
		textFile.create(stream, false, getMonitor());
		stream.close();
		ensureExists(textFile);
		IFolder newFolderDestination = destinationFolder.getFolder("NewFolder");
		newFolder.move(newFolderDestination.getFullPath(), false, getMonitor());
		ensureDoesNotExist(newFolder);
		ensureExists(newFolderDestination);
	}

	@Test
	public void testMoveFileFromZipFile() throws Exception {
		IFile textFile = ZipFileSystemTestSetup.firstProject
				.getFile(zipFileName + "/" + ZipFileSystemTestSetup.TEXT_FILE_NAME);
		ensureExists(textFile);
		IFile destinationFile = ZipFileSystemTestSetup.firstProject.getFile(ZipFileSystemTestSetup.TEXT_FILE_NAME);
		textFile.move(destinationFile.getFullPath(), false, getMonitor());

		// Verify that the file exists at the new location
		ensureExists(destinationFile);
		assertTextFileContent(destinationFile, "Hello World!");

		// Verify that the file does not exist at the old location
		ensureDoesNotExist(textFile);
	}

	@Test
	public void testMoveFolderFromZipFile() throws Exception {
		IFolder openedZipFile = ZipFileSystemTestSetup.firstProject.getFolder(zipFileName);
		IFolder newFolder = openedZipFile.getFolder("NewFolder");
		ensureDoesNotExist(newFolder);
		newFolder.create(false, true, getMonitor());
		ensureExists(newFolder);
		IFolder folderDestination = ZipFileSystemTestSetup.firstProject.getFolder("NewFolder");
		newFolder.move(folderDestination.getFullPath(), false, getMonitor());
		ensureDoesNotExist(newFolder);
		ensureExists(folderDestination);
	}

	@Test
	public void testMoveFolderWithContentFromZipFile() throws Exception {
		IFolder openedZipFile = ZipFileSystemTestSetup.firstProject.getFolder(zipFileName);
		IFolder newFolder = openedZipFile.getFolder("NewFolder");
		ensureDoesNotExist(newFolder);
		newFolder.create(false, true, getMonitor());
		ensureExists(newFolder);
		IFile textFile = newFolder.getFile("NewFile.txt");
		ensureDoesNotExist(textFile);
		String text = "Foo";
		InputStream stream = new ByteArrayInputStream(text.getBytes());
		textFile.create(stream, false, getMonitor());
		stream.close();
		ensureExists(textFile);
		IFolder folderDestination = ZipFileSystemTestSetup.firstProject.getFolder("NewFolder");
		newFolder.move(folderDestination.getFullPath(), false, getMonitor());
		ensureDoesNotExist(newFolder);
		ensureExists(folderDestination);
	}


	@Test
	public void testMoveFileInsideOfZipFile() throws Exception {
		IFolder openedZipFile = ZipFileSystemTestSetup.firstProject.getFolder(zipFileName);
		IFolder destinationFolder = openedZipFile.getFolder("destinationFolder");
		ensureDoesNotExist(destinationFolder);
		destinationFolder.create(false, true, getMonitor());
		ensureExists(destinationFolder);
		IFile textFile = openedZipFile.getFile(ZipFileSystemTestSetup.TEXT_FILE_NAME);
		ensureExists(textFile);
		IFile fileDestination = destinationFolder.getFile(ZipFileSystemTestSetup.TEXT_FILE_NAME);
		ensureDoesNotExist(fileDestination);
		textFile.move(fileDestination.getFullPath(), false, getMonitor());
		ensureExists(fileDestination);
		ensureDoesNotExist(textFile);
	}



	@Test
	public void testMoveZipFileIntoZipFile() throws Exception {
		IFolder openedZipFile = ZipFileSystemTestSetup.firstProject.getFolder(zipFileName);
		// create and expand second ZipFile
		String newZipFileName = zipFileName.replace(".", "New.");
		IFile newZipFile = ZipFileSystemTestSetup.firstProject.getFile(newZipFileName);
		ensureDoesNotExist(newZipFile);
		ZipFileSystemTestSetup.copyZipFileIntoJavaProject(ZipFileSystemTestSetup.firstProject, newZipFileName);
		ensureExists(newZipFile);
		ZipFileSystemTestUtil.openZipFile(newZipFile);
		IFolder newOpenedZipFile = ZipFileSystemTestSetup.firstProject.getFolder(newZipFileName);
		ensureExists(newOpenedZipFile);
		// move second ZipFile into first ZipFile
		IFolder newOpenedZipFileDestination = openedZipFile.getFolder(newZipFileName);
		newOpenedZipFile.move(newOpenedZipFileDestination.getFullPath(), false, getMonitor());
		ensureExists(newOpenedZipFileDestination);
		ensureDoesNotExist(newOpenedZipFile);
	}

	/**
	 * When moving or expanding an opened zip file that contains a folder with
	 * content. errors can occur. This is because the local name of the resources
	 * inside the folder contains "\" seperators that are not allowed when
	 * refreshing the Workspace. This test checks if this specific error is handeled
	 * correctly in RefreshLocalVisitor#visit()
	 */
	@Test
	public void testMoveZipFileWithFolder() throws Exception {
		IFolder openedZipFile = ZipFileSystemTestSetup.firstProject.getFolder(zipFileName);
		String contentFolderPath = zipFileName + "/" + "Folder";
		IFolder contentFolder = ZipFileSystemTestSetup.firstProject.getFolder(contentFolderPath);
		ensureDoesNotExist(contentFolder);
		contentFolder.create(false, true, getMonitor());
		ensureExists(contentFolder);
		String text = "Foo";
		InputStream stream = new ByteArrayInputStream(text.getBytes());
		IFile textFile = ZipFileSystemTestSetup.firstProject.getFile(contentFolderPath + "/" + "textFile");
		ensureDoesNotExist(textFile);
		textFile.create(stream, false, getMonitor());
		ensureExists(textFile);
		IFolder destinationFolder = ZipFileSystemTestSetup.firstProject.getFolder("destinationFolder");
		ensureDoesNotExist(destinationFolder);
		destinationFolder.create(false, true, getMonitor());
		ensureExists(destinationFolder);
		IFolder zipFileDestination = ZipFileSystemTestSetup.firstProject.getFolder("destinationFolder/" + zipFileName);
		ensureDoesNotExist(zipFileDestination);
		openedZipFile.move(zipFileDestination.getFullPath(), false, getMonitor());
		ensureExists(zipFileDestination);
	}
}
