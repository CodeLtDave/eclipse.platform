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
import static org.eclipse.core.tests.filesystem.zip.ZipFileSystemTestUtil.getMonitor;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IAccessRule;
import org.eclipse.jdt.core.IClasspathAttribute;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.JavaRuntime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 *
 */
public class IllegalCompressionMethodTest {

	private static IProject project;
	private static final String PROJECT_NAME = "Project";
	private static final String ARCHIVE_NAME = "EnhancedDeflated.zip";

	@Before
	public void setup() throws Exception {
		initializeProject();
		copyZipIntoJavaProject(ARCHIVE_NAME);
	}

	@After
	public void teardown() throws Exception {
		ZipFileSystemTestSetup.teardown();
	}

	@Test
	public void testExpandEnhancedDeflatedArchive() throws CoreException, URISyntaxException, IOException {
		IFile archiveFile = project.getFile(ARCHIVE_NAME);
		ensureExists(archiveFile);
		try {
			ZipFileSystemTestUtil.expandZipFile(archiveFile);
		} catch (IOException e) {
			assertEquals("invalid CEN header (unsupported compression method: 9)",
					e.getMessage());
		}
	}

	private IProject initializeProject() throws CoreException, JavaModelException {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		project = workspace.getRoot().getProject(PROJECT_NAME);
		project.create(getMonitor());
		project.open(getMonitor());
		IProjectDescription description = project.getDescription();
		description.setNatureIds(new String[] { JavaCore.NATURE_ID });
		project.setDescription(description, getMonitor());
		IJavaProject JavaProject = JavaCore.create(project);
		IFolder srcFolder = project.getFolder("src");
		if (!srcFolder.exists()) {
			srcFolder.create(false, true, getMonitor());
		}
		IFolder binFolder = project.getFolder("bin");
		if (!binFolder.exists()) {
			binFolder.create(false, true, getMonitor());
		}
		JavaProject.setOutputLocation(binFolder.getFullPath(), getMonitor());
		// Set Java compliance level and JRE container
		JavaProject.setOption(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_8);
		JavaProject.setOption(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_8);
		JavaProject.setOption(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, JavaCore.VERSION_1_8);
		// Add the JRE container to the classpath
		IClasspathEntry jreContainerEntry = JavaCore.newContainerEntry(new Path(JavaRuntime.JRE_CONTAINER),
				new IAccessRule[0],
				new IClasspathAttribute[] { JavaCore.newClasspathAttribute("owner.project.facets", "java") }, false);
		IClasspathEntry srcEntry = JavaCore.newSourceEntry(srcFolder.getFullPath());
		JavaProject.setRawClasspath(new IClasspathEntry[] { jreContainerEntry, srcEntry }, getMonitor());
		project.refreshLocal(IResource.DEPTH_INFINITE, getMonitor());
		return project;
	}

	private static void copyZipIntoJavaProject(String zipFileName) throws Exception {
		// Resolve the source file URL from the plugin bundle
		URL zipFileUrl = Platform.getBundle("org.eclipse.core.tests.resources")
				.getEntry("resources/ZipFileSystem/" + zipFileName);
		// Ensure proper conversion from URL to URI to Path
		URL resolvedURL = FileLocator.resolve(zipFileUrl); // Resolves any redirection or bundling
		java.nio.file.Path sourcePath;
		try {
			// Convert URL to URI to Path correctly handling spaces and special characters
			URI resolvedURI = resolvedURL.toURI();
			sourcePath = Paths.get(resolvedURI);
		} catch (URISyntaxException e) {
			throw new IOException("Failed to resolve URI for the ZIP file", e);
		}

		// Determine the target location within the project
		java.nio.file.Path targetPath = Paths.get(project.getLocation().toOSString(), zipFileName);

		// Copy the file using java.nio.file.Files
		Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);

		// Refresh the project to make Eclipse aware of the new file
		project.refreshLocal(IResource.DEPTH_INFINITE, null);
	}
}

