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
import static org.mockito.Mockito.mock;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IAccessRule;
import org.eclipse.jdt.core.IClasspathAttribute;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.examples.filesystem.ExpandZipHandler;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *
 */
public class MoveTest {

	private static IProject secondProject;
	private static final String SECOND_PROJECT_NAME = "SecondProject";

	@Before
	public void setup() throws Exception {
		ZipFileSystemTestSetup.setup();
		expandZipFile(ZipFileSystemTestSetup.project.getFile(ZipFileSystemTestSetup.ZIP_FILE_VIRTUAL_FOLDER_NAME));
		// Second project is needed for some tests
		initializeSecondProject();
	}

	@After
	public void teardown() throws Exception {
		ZipFileSystemTestSetup.teardown();
		secondProject.delete(false, getMonitor());
	}

	private void expandZipFile(IFile file) throws Exception {
		ExpandZipHandler expandZipHandler = new ExpandZipHandler();
		Shell shell = mock(Shell.class);
		expandZipHandler.expandZip(file, shell);
		IFolder virtualFolder = ZipFileSystemTestSetup.project.getFolder(file.getName());
		Assert.assertTrue("ZIP file should exist before deletion", virtualFolder.exists());
	}

	@Test
	public void testMoveArchiveWithinProject() throws CoreException, IOException {
		IFolder destinationFolder = ZipFileSystemTestSetup.project.getFolder("destinationFolder");
		destinationFolder.create(false, true, getMonitor());
		IFolder destination = ZipFileSystemTestSetup.project
				.getFolder("destinationFolder/" + ZipFileSystemTestSetup.ZIP_FILE_VIRTUAL_FOLDER_NAME);
		IFolder virtualFolder = ZipFileSystemTestSetup.project
				.getFolder(ZipFileSystemTestSetup.ZIP_FILE_VIRTUAL_FOLDER_NAME);
		virtualFolder.move(destination.getFullPath(), false, getMonitor());

		// Verify that the folder exists at the new location and not at the old location
		// anymore
		IFolder newFolder = ZipFileSystemTestSetup.project
				.getFolder(destinationFolder.getName() + "/" + ZipFileSystemTestSetup.ZIP_FILE_VIRTUAL_FOLDER_NAME);
		ensureExists(newFolder);
		ensureDoesNotExist(virtualFolder);
	}

	@Test
	public void testMoveArchiveToOtherProject() throws CoreException, IOException {
		IFolder destination = secondProject.getFolder(ZipFileSystemTestSetup.ZIP_FILE_VIRTUAL_FOLDER_NAME);
		IFolder virtualFolder = ZipFileSystemTestSetup.project
				.getFolder(ZipFileSystemTestSetup.ZIP_FILE_VIRTUAL_FOLDER_NAME);
		virtualFolder.move(destination.getFullPath(), false, getMonitor());

		// Verify that the folder exists at the new location and not at the old location
		// anymore
		IFolder newFolder = secondProject.getFolder(ZipFileSystemTestSetup.ZIP_FILE_VIRTUAL_FOLDER_NAME);
		ensureExists(newFolder);
		ensureDoesNotExist(virtualFolder);
		secondProject.delete(false, getMonitor());
	}

	@Test
	public void testMoveArchiveToOtherProjectFolder() throws CoreException, IOException {
		IFolder destinationFolder = secondProject.getFolder("destinationFolder");
		destinationFolder.create(false, true, getMonitor());
		IFolder destination = secondProject
				.getFolder("destinationFolder/" + ZipFileSystemTestSetup.ZIP_FILE_VIRTUAL_FOLDER_NAME);
		IFolder virtualFolder = ZipFileSystemTestSetup.project
				.getFolder(ZipFileSystemTestSetup.ZIP_FILE_VIRTUAL_FOLDER_NAME);
		virtualFolder.move(destination.getFullPath(), false, getMonitor());

		// Verify that the folder exists at the new location and not at the old location
		// anymore
		IFolder newFolder = secondProject
				.getFolder(destinationFolder.getName() + "/" + ZipFileSystemTestSetup.ZIP_FILE_VIRTUAL_FOLDER_NAME);
		ensureExists(newFolder);
		ensureDoesNotExist(virtualFolder);
		secondProject.delete(false, getMonitor());
	}

	@Test
	public void testMoveFileIntoZip() throws Exception {
		IFile textFile = ZipFileSystemTestSetup.project.getFile("NewFile.txt");
		ensureDoesNotExist(textFile);
		String text = "Foo";
		InputStream stream = new ByteArrayInputStream(text.getBytes());
		textFile.create(stream, false, getMonitor());
		stream.close();
		ensureExists(textFile);
		IFile destinationFile = ZipFileSystemTestSetup.project
				.getFile(ZipFileSystemTestSetup.ZIP_FILE_VIRTUAL_FOLDER_NAME + "/" + "NewFile.txt");
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
	public void testMoveFileFromZip() throws Exception {
		IFile textFile = ZipFileSystemTestSetup.project.getFile(
				ZipFileSystemTestSetup.ZIP_FILE_VIRTUAL_FOLDER_NAME + "/" + ZipFileSystemTestSetup.TEXT_FILE_NAME);
		ensureExists(textFile);
		IFile destinationFile = ZipFileSystemTestSetup.project.getFile(ZipFileSystemTestSetup.TEXT_FILE_NAME);
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

	private IProject initializeSecondProject() throws CoreException, JavaModelException {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		secondProject = workspace.getRoot().getProject(SECOND_PROJECT_NAME);
		secondProject.create(getMonitor());
		secondProject.open(getMonitor());
		IProjectDescription description = secondProject.getDescription();
		description.setNatureIds(new String[] { JavaCore.NATURE_ID });
		secondProject.setDescription(description, getMonitor());
		IJavaProject secondJavaProject = JavaCore.create(secondProject);
		IFolder srcFolder = secondProject.getFolder("src");
		if (!srcFolder.exists()) {
			srcFolder.create(false, true, getMonitor());
		}
		IFolder binFolder = secondProject.getFolder("bin");
		if (!binFolder.exists()) {
			binFolder.create(false, true, getMonitor());
		}
		secondJavaProject.setOutputLocation(binFolder.getFullPath(), getMonitor());
		// Set Java compliance level and JRE container
		secondJavaProject.setOption(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_8);
		secondJavaProject.setOption(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_8);
		secondJavaProject.setOption(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, JavaCore.VERSION_1_8);
		// Add the JRE container to the classpath
		IClasspathEntry jreContainerEntry = JavaCore.newContainerEntry(new Path(JavaRuntime.JRE_CONTAINER),
				new IAccessRule[0],
				new IClasspathAttribute[] { JavaCore.newClasspathAttribute("owner.project.facets", "java") }, false);
		IClasspathEntry srcEntry = JavaCore.newSourceEntry(srcFolder.getFullPath());
		secondJavaProject.setRawClasspath(new IClasspathEntry[] { jreContainerEntry, srcEntry }, getMonitor());
		secondProject.refreshLocal(IResource.DEPTH_INFINITE, getMonitor());
		return secondProject;
	}
}
