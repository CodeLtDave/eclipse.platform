/*******************************************************************************
 * Copyright (c) 2024 Vector Informatik GmbH and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *******************************************************************************/
package org.eclipse.core.tests.filesystem.zip;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.tests.harness.FussyProgressMonitor;
import org.eclipse.ui.handlers.ZipCollapser;
import org.eclipse.ui.handlers.ZipExpander;
import org.junit.Assert;

final class ZipFileSystemTestUtil {

	private ZipFileSystemTestUtil() {
	}

	static void ensureExists(IResource resource) throws CoreException, IOException {
		switch (resource.getType()) {
		case IResource.FILE: {
			IFileStore fileStore = EFS.getStore(resource.getLocationURI());
			ensureExistsInFileSystem(fileStore);
			ensureExistsInWorkspace((IFile) resource);
			break;
		}
		case IResource.FOLDER: {
			IFileStore fileStore = EFS.getStore(resource.getLocationURI());
			ensureExistsInFileSystem(fileStore);
			ensureExistsInWorkspace((IFolder) resource);
			break;
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + resource.getType());
		}
	}

	static void ensureDoesNotExist(IResource resource) throws CoreException, IOException {
		switch (resource.getType()) {
		case IResource.FILE: {
			IFileStore fileStore = EFS.getStore(resource.getLocationURI());
			ensureDoesNotExistInFileSystem(fileStore);
			ensureDoesNotExistInWorkspace((IFile) resource);
			break;
		}
		case IResource.FOLDER: {
			IFileStore fileStore = EFS.getStore(resource.getLocationURI());
			ensureDoesNotExistInFileSystem(fileStore);
			ensureDoesNotExistInWorkspace((IFolder) resource);
			break;
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + resource.getType());
		}
	}

	private static void ensureDoesNotExistInFileSystem(IFileStore store) throws CoreException {
		assertTrue("store was not properly deleted: " + store, !store.fetchInfo().exists());
	}

	private static void ensureExistsInFileSystem(IFileStore store) throws CoreException, IOException {
			final IFileInfo info = store.fetchInfo();
			assertTrue("file info for store does not exist: " + store, info.exists());
		}

		private static void ensureDoesNotExistInWorkspace(IFile file) throws CoreException {
			assertTrue("file was not properly deleted: " + file, !file.exists());
		}

		private static void ensureDoesNotExistInWorkspace(IFolder folder) throws CoreException {
			assertTrue("folder was not properly deleted: " + folder, !folder.exists());
		}

		private static void ensureExistsInWorkspace(IFile file) throws CoreException, IOException {
			assertTrue("file does not exist in workspace: " + file, file.exists());
		}

		private static void ensureExistsInWorkspace(IFolder folder) throws CoreException, IOException {
			assertTrue("folder does not exist in workspace: " + folder, folder.exists());
		}

	static IProgressMonitor getMonitor() {
		return new FussyProgressMonitor();
	}

	static void expandZipFile(IFile file) throws Exception {
		ZipExpander.expandZip(file);
		IFolder virtualFolder = ZipFileSystemTestSetup.project.getFolder(file.getName());
		Assert.assertTrue("ZIP file should exist before deletion", virtualFolder.exists());
	}

	static void collapseZipFile(IFolder folder) throws Exception {
		ZipCollapser.collapseZip(folder);
		IFile zipFile = ZipFileSystemTestSetup.project.getFile(folder.getName());
		ensureExists(zipFile);
	}

	static void printContents(IContainer container, String indent) throws CoreException {
		IResource[] members = container.members();
		for (IResource member : members) {
			if (member instanceof IFile) {
				System.out.println(indent + "File: " + member.getName());
			} else if (member instanceof IContainer) { // This can be IFolder or IProject
				System.out.println(indent + "Folder: " + member.getName());
				printContents((IContainer) member, indent + "  "); // Recursively print contents
			}
		}
	}

}
