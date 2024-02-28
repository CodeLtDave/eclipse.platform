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

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IAccessRule;
import org.eclipse.jdt.core.IClasspathAttribute;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.JavaRuntime;

/**
 *
 */
public class ZipFileSystemTestSetup {

	private ZipFileSystemTestSetup() {
	}

	static final String PROJECT_NAME = "TestProject";
	static final String ZIP_FILE_VIRTUAL_FOLDER_NAME = "BasicText.zip"; // Assuming the ZIP is represented as
																				// this folder
	static final String TEXT_FILE_NAME = "Text.txt";
	static IProject project;
	static IJavaProject javaProject;
	static IProgressMonitor progressMonitor = new NullProgressMonitor();

	static void setup() throws Exception {
		createProject();
		createJavaProject();
		refreshProject();
		copyZipIntoJavaProject(ZIP_FILE_VIRTUAL_FOLDER_NAME);
		refreshProject();
		ZipFileSystemTestUtil.expandZipFile(project.getFile(ZIP_FILE_VIRTUAL_FOLDER_NAME));
	}

	static void teardown() throws Exception {
		deleteProject();
	}

	private static void deleteProject() throws CoreException {
		if (project != null && project.exists()) {
			project.delete(true, true, progressMonitor);
			project = null;
		}
	}

	private static void createProject() throws CoreException {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		project = workspace.getRoot().getProject(PROJECT_NAME);

		if (!project.exists()) {
			project.create(progressMonitor);
		}
		project.open(progressMonitor);
	}

	private static void createJavaProject() throws CoreException, JavaModelException {
		IProjectDescription description = project.getDescription();
		description.setNatureIds(new String[] { JavaCore.NATURE_ID });
		project.setDescription(description, progressMonitor);

		javaProject = JavaCore.create(project);

		IFolder srcFolder = project.getFolder("src");
		if (!srcFolder.exists()) {
			srcFolder.create(false, true, progressMonitor);
		}

		IFolder binFolder = project.getFolder("bin");
		if (!binFolder.exists()) {
			binFolder.create(false, true, progressMonitor);
		}
		javaProject.setOutputLocation(binFolder.getFullPath(), progressMonitor);

		// Set Java compliance level and JRE container
		javaProject.setOption(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_8);
		javaProject.setOption(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_8);
		javaProject.setOption(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, JavaCore.VERSION_1_8);

		// Add the JRE container to the classpath
		IClasspathEntry jreContainerEntry = JavaCore.newContainerEntry(new Path(JavaRuntime.JRE_CONTAINER),
				new IAccessRule[0],
				new IClasspathAttribute[] { JavaCore.newClasspathAttribute("owner.project.facets", "java") }, false);
		IClasspathEntry srcEntry = JavaCore.newSourceEntry(srcFolder.getFullPath());

		javaProject.setRawClasspath(new IClasspathEntry[] { jreContainerEntry, srcEntry }, null);
	}

	private static void refreshProject() {
		try {
			if (project.exists() && project.isOpen()) {
				// Refreshing the specific project
				project.refreshLocal(IResource.DEPTH_INFINITE, null);
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
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
