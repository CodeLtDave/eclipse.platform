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
package org.eclipse.core.tests.internal.localstore;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class UnifiedTreeNodeTest {

	private IProject project;
	private IFile testFile1;
	private IFile testFile2;
	private IFolder virtualFolder1;
	private IFolder virtualFolder2;

	@Before
	public void setUp() throws CoreException, URISyntaxException {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		project = root.getProject("TestProject");
		project.create(null);
		project.open(null);

		// Create two files in the project
		testFile1 = project.getFile("TestFile1.txt");
		testFile1.create(new ByteArrayInputStream("Test content".getBytes(StandardCharsets.UTF_8)), IResource.NONE,
				null);

		testFile2 = project.getFile("TestFile2.txt");
		testFile2.create(new ByteArrayInputStream("Test content".getBytes(StandardCharsets.UTF_8)), IResource.NONE,
				null);

		URI uri1 = new URI("file", null, "/", testFile1.getLocationURI().toString(), null); //$NON-NLS-1$ //$NON-NLS-2$
		virtualFolder1 = testFile1.getParent().getFolder(IPath.fromOSString(testFile1.getName()));
		virtualFolder1.createLink(uri1, IResource.REPLACE, null);

		URI uri2 = new URI("file", null, testFile2.getLocationURI().getPath() + "/", null);
		virtualFolder2 = project.getFolder("VirtualFolder2");
		virtualFolder2.createLink(uri2, IResource.REPLACE, null);
	}

	@After
	public void tearDown() throws CoreException {
		if (project.exists()) {
			project.delete(true, null);
		}
	}

	@Test
	public void testRemoveLinkedFolderRevealsLocalFile() throws CoreException {
		// Refresh the project
		project.refreshLocal(IResource.DEPTH_INFINITE, null);

		// Delete the first virtual folder
		virtualFolder1.delete(true, null);

		// Refresh the project
		project.refreshLocal(IResource.DEPTH_INFINITE, null);


		IFile a = project.getFile(testFile2.getLocation());
	}
}
