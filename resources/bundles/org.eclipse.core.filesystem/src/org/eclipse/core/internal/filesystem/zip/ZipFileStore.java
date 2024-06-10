/*******************************************************************************
 * Copyright (c) 2022 IBM Corporation and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors: IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.core.internal.filesystem.zip;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.provider.FileInfo;
import org.eclipse.core.filesystem.provider.FileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * File store implementation representing a file or directory inside a zip file.
 */
public class ZipFileStore extends FileStore {
	/**
	 * The path of this store within the zip file.
	 */
	private final IPath path;

	/**
	 * The file store that represents the actual zip file.
	 */
	private final IFileStore rootStore;

	/**
	 * Creates a new zip file store.
	 */
	public ZipFileStore(IFileStore rootStore, IPath path) {
		this.rootStore = rootStore;
		this.path = path.makeRelative();
	}

	private ZipEntry[] childEntries(IProgressMonitor monitor) throws CoreException {
		List<ZipEntry> entryList = new ArrayList<>();
		String myName = path.toString();

		try (FileSystem zipFs = openZipFileSystem()) {
			Path zipRoot = zipFs.getPath(myName);
			Files.walkFileTree(zipRoot, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					String entryName = zipRoot.relativize(file).toString();
					if (!Files.isDirectory(file)) {
						// For files, read attributes and create ZipEntry
						ZipEntry zipEntry = new ZipEntry(entryName);
						zipEntry.setSize(attrs.size());
						zipEntry.setTime(attrs.lastModifiedTime().toMillis());
						// Compressed size is not directly available; method is set based on ZIP standard
						zipEntry.setMethod(ZipEntry.DEFLATED);
						entryList.add(zipEntry);
					} else {
						// For directories, simply add them with a trailing slash
						entryList.add(new ZipEntry(entryName + "/")); //$NON-NLS-1$
					}
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
					// Include directories only if they are not the root directory
					if (!dir.equals(zipRoot)) {
						String dirName = zipRoot.relativize(dir).toString() + "/"; //$NON-NLS-1$
						entryList.add(new ZipEntry(dirName));
					}
					return FileVisitResult.CONTINUE;
				}
			});
		} catch (IOException | URISyntaxException e) {
			throw new CoreException(new Status(IStatus.ERROR, "YourPluginID", "Error reading ZIP file", e)); //$NON-NLS-1$ //$NON-NLS-2$
		}

		return entryList.toArray(new ZipEntry[0]);
	}



	@Override
	public IFileInfo[] childInfos(int options, IProgressMonitor monitor) throws CoreException {
		ZipEntry[] entries = childEntries(monitor);
		int entryCount = entries.length;
		IFileInfo[] infos = new IFileInfo[entryCount];
		for (int i = 0; i < entryCount; i++) {
			infos[i] = convertZipEntryToFileInfo(entries[i]);
		}
		return infos;
	}

	@Override
	public String[] childNames(int options, IProgressMonitor monitor) throws CoreException {
		ZipEntry[] entries = childEntries(monitor);
		int entryCount = entries.length;
		String[] names = new String[entryCount];
		for (int i = 0; i < entryCount; i++) {
			names[i] = computeName(entries[i]);
		}
		return names;
	}

	/**
	 * Computes the simple file name for a given zip entry.
	 */
	private String computeName(ZipEntry entry) {
		// the entry name is a relative path, with an optional trailing
		// separator
		// We need to strip off the trailing slash, and then take everything
		// after the
		// last separator as the name
		String name = entry.getName();
		int end = name.length() - 1;
		if (name.charAt(end) == '/') {
			end--;
		}
		return name.substring(0, end + 1);
	}

	/**
	 * Creates a file info object corresponding to a given zip entry
	 *
	 * @param entry the zip entry
	 * @return The file info for a zip entry
	 */
	private IFileInfo convertZipEntryToFileInfo(ZipEntry entry) {
		FileInfo info = new FileInfo(computeName(entry));
		if (entry.isDirectory()) {
			info.setLastModified(EFS.NONE);
		} else {
			info.setLastModified(entry.getTime());
		}

		info.setExists(true);
		info.setDirectory(entry.isDirectory());
		info.setLength(entry.getSize());
		return info;
	}

	/**
	 * @return A directory info for this file store
	 */
	private IFileInfo createDirectoryInfo(String name) {
		FileInfo result = new FileInfo(name);
		result.setExists(true);
		result.setDirectory(true);
		return result;
	}

	@Override
	public void delete(int options, IProgressMonitor monitor) throws CoreException {
		try (FileSystem zipFs = openZipFileSystem()) {
			Path fileToDelete = zipFs.getPath(path.toString());
			if (Files.exists(fileToDelete)) {
				Files.delete(fileToDelete);
			}
		} catch (IOException | URISyntaxException e) {
			throw new CoreException(new Status(IStatus.ERROR, "org.eclipse.core.internal.filesystem.zip", "Error deleting file from zip", e)); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	@Override
	public IFileInfo fetchInfo(int options, IProgressMonitor monitor) throws CoreException {
		try (ZipInputStream in = new ZipInputStream(rootStore.openInputStream(EFS.NONE, monitor))) {
			String myPath = path.toString();
			ZipEntry current;
			while ((current = in.getNextEntry()) != null) {
				String currentPath = current.getName();
				if (myPath.equals(currentPath)) {
					return convertZipEntryToFileInfo(current);
				}
				// directories don't always have their own entry, but it is
				// implied by the existence of a child
				if (isAncestor(myPath, currentPath)) {
					return createDirectoryInfo(getName());
				}
			}
		} catch (IOException e) {
			throw new CoreException(Status.error("Could not read file: " + rootStore.toString(), e)); //$NON-NLS-1$
		}
		// does not exist
		return new FileInfo(getName());
	}

	/**
	 * Finds the zip entry with the given name in this zip file. Returns the
	 * entry and leaves the input stream open positioned at the beginning of the
	 * bytes of that entry. Returns null if the entry could not be found.
	 */
	private ZipEntry findEntry(String name, ZipInputStream in) throws IOException {
		ZipEntry current;
		while ((current = in.getNextEntry()) != null) {
			if (current.getName().equals(name)) {
				return current;
			}
		}
		return null;
	}

	@Override
	public IFileStore getChild(String name) {
		return new ZipFileStore(rootStore, path.append(name));
	}

	@Override
	public String getName() {
		String name = path.lastSegment();
		return name == null ? "" : name; //$NON-NLS-1$
	}

	@Override
	public IFileStore getParent() {
		if (path.segmentCount() > 0) {
			return new ZipFileStore(rootStore, path.removeLastSegments(1));
		}
		// the root entry has no parent
		return null;
	}

	/**
	 * Returns whether ancestor is a parent of child.
	 *
	 * @param ancestor the potential ancestor
	 * @param child the potential child
	 * @return <code>true</code> or <code>false</code>
	 */

	private boolean isAncestor(String ancestor, String child) {
		// children will start with myName and have no child path
		int ancestorLength = ancestor.length();
		if (ancestorLength == 0) {
			return true;
		}
		return child.startsWith(ancestor) && child.length() > ancestorLength && child.charAt(ancestorLength) == '/';
	}

	private boolean isNested() {
		return this.rootStore instanceof ZipFileStore;
	}

	@Override
	public IFileStore mkdir(int options, IProgressMonitor monitor) throws CoreException {
		URI zipUri;
		try {
			zipUri = new URI("jar:" + rootStore.toURI().toString() + "!/"); //$NON-NLS-1$ //$NON-NLS-2$
		} catch (URISyntaxException e) {
			throw new CoreException(new Status(IStatus.ERROR, "org.eclipse.core.internal.filesystem.zip", "Invalid ZIP file URI", e)); //$NON-NLS-1$ //$NON-NLS-2$
		}

		Map<String, String> env = new HashMap<>();
		env.put("create", "false"); //$NON-NLS-1$ //$NON-NLS-2$

		// Assuming the directory to create is represented by 'this.path'
		try (FileSystem zipFs = FileSystems.newFileSystem(zipUri, env)) {
			Path dirInZipPath = zipFs.getPath(this.path.toString());
			if (Files.notExists(dirInZipPath)) {
				Files.createDirectories(dirInZipPath);

				// To ensure the directory is actually added to the ZIP, we
				// might need to add a temporary file
				// in this directory. This is a workaround and should be used
				// with caution.
				Path tempFileInDir = dirInZipPath.resolve(".keep"); //$NON-NLS-1$
				Files.createFile(tempFileInDir);

				// Immediately delete the temporary file after creation to just
				// keep the directory
				Files.delete(tempFileInDir);
			}
		} catch (IOException e) {
			throw new CoreException(new Status(IStatus.ERROR, "org.eclipse.core.internal.filesystem.zip", "Error creating directory in ZIP file", e)); //$NON-NLS-1$ //$NON-NLS-2$
		}

		// Return a file store representing the newly created directory.
		return new ZipFileStore(rootStore, this.path);
	}

	@Override
	public void move(IFileStore destination, int options, IProgressMonitor monitor) throws CoreException {
		//if destination is no archive
		if (!(destination instanceof ZipFileStore)) {
			super.move(destination, options, monitor);
			return;
		}
		ZipFileStore destZipFileStore = (ZipFileStore) destination;

		try (FileSystem srcFs = openZipFileSystem(); FileSystem destFs = destZipFileStore.openZipFileSystem()) {

			Path srcPath = srcFs.getPath(this.path.toString());
			Path destPath = destFs.getPath(destZipFileStore.path.toString());

			// Ensure the parent directories of the destination path exist.
			if (destPath.getParent() != null) {
				Files.createDirectories(destPath.getParent());
			}

			// Attempt to move the file or directory.
			// Note: This conceptual code does not account for the actual limitations of ZIP FileSystem.
			Files.move(srcPath, destPath, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException | URISyntaxException e) {
			throw new CoreException(new Status(IStatus.ERROR, "org.eclipse.core.internal.filesystem.zip", "Error moving entry within ZIP", e)); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}


	@Override
	public InputStream openInputStream(int options, IProgressMonitor monitor) throws CoreException {
		try (ZipInputStream in = new ZipInputStream(rootStore.openInputStream(EFS.NONE, monitor))) {
			ZipEntry entry = findEntry(path.toString(), in);
			if (entry == null) {
				throw new CoreException(Status.error("File not found: " + rootStore.toString())); //$NON-NLS-1$
			}
			if (entry.isDirectory()) {
				throw new CoreException(Status.error("Resource is not a file: " + rootStore.toString())); //$NON-NLS-1$
			}
			return in;
		} catch (IOException e) {
			throw new CoreException(Status.error("Could not read file: " + rootStore.toString(), e)); //$NON-NLS-1$
		}
	}

	@Override
	public OutputStream openOutputStream(int options, IProgressMonitor monitor) {
		// Creating a ByteArrayOutputStream to capture the data written to the
		// OutputStream
		ByteArrayOutputStream baos = new ByteArrayOutputStream() {
			@Override
			public void close() throws IOException {
				try (FileSystem zipFs = openZipFileSystem()) {
					Path entryPath = zipFs.getPath(path.toString());
					// Ensure parent directories exist
					Path parentPath = entryPath.getParent();
					if (parentPath != null) {
						Files.createDirectories(parentPath);
					}
					// Write the ByteArrayOutputStream's data to the entry
					// in the ZIP file
					Files.write(entryPath, this.toByteArray(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

				} catch (Exception e) {
					throw new IOException("Failed to integrate data into ZIP file", e); //$NON-NLS-1$
				}
			}
		};

		return baos;
	}

	private FileSystem openZipFileSystem() throws IOException, URISyntaxException {
		ZipFileStore store = this;
		URI nioURI = toNioURI();
		while (store.isNested()) {
			System.out.println("Nested"); //$NON-NLS-1$

		}

		Map<String, Object> env = new HashMap<>();
		env.put("create", "false"); //$NON-NLS-1$ //$NON-NLS-2$
		FileSystem fs;
		try {
			fs = FileSystems.getFileSystem(nioURI);
		} catch (FileSystemNotFoundException e) {
			return FileSystems.newFileSystem(nioURI, env);
		}
		return fs;

	}

	@Override
	public void putInfo(IFileInfo info, int options, IProgressMonitor monitor) throws CoreException {
		if (monitor != null) {
			monitor.beginTask("Updating Zip Entry Information", 1); //$NON-NLS-1$
		}
		try {
			// Check options for what information is requested to be updated
			if ((options & EFS.SET_ATTRIBUTES) != 0) {
				boolean isHidden = info.getAttribute(EFS.ATTRIBUTE_HIDDEN);
				boolean isArchive = info.getAttribute(EFS.ATTRIBUTE_ARCHIVE);

				if (ZipFileSystem.getOS().startsWith("Windows")) { //$NON-NLS-1$
					Files.setAttribute(filePath, "dos:hidden", isHidden); //$NON-NLS-1$
					Files.setAttribute(filePath, "dos:archive", isArchive); //$NON-NLS-1$
				}
			}
			if ((options & EFS.SET_LAST_MODIFIED) != 0) {
				FileTime lastModified = FileTime.fromMillis(info.getLastModified());
				Files.setLastModifiedTime(filePath, lastModified);
			}

		} catch (Exception e) {
			throw new CoreException(new Status(IStatus.ERROR, "org.eclipse.core.internal.filesystem.zip", "Error updating ZIP file entry information", e)); //$NON-NLS-1$ //$NON-NLS-2$
		} finally {
			if (monitor != null) {
				monitor.done();
			}
		}
	}

	@Override
	public URI toURI() {
		try {
			return new URI(ZipFileSystem.SCHEME_ZIP, null, path.makeAbsolute().toString(), rootStore.toURI().toString(), null);
		} catch (URISyntaxException e) {
			// should not happen
			throw new RuntimeException(e);
		}
	}

	private URI toNioURI() throws URISyntaxException {
		String nioScheme = "jar:"; //$NON-NLS-1$
		String path = rootStore.toURI().toString();
		String suffix = "!/"; //$NON-NLS-1$
		return new URI(nioScheme + path + suffix);
	}
}
