/*******************************************************************************
 * Copyright (c) 2022 IBM Corporation and others.
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
package org.eclipse.core.internal.filesystem.zip;

import java.net.URI;
import java.net.URISyntaxException;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.provider.FileSystem;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

/**
 * @since 1.11
 */
public class ZipFileSystem extends FileSystem {
	/**
	 * Scheme constant (value "zip") indicating the zip file system scheme.
	 */
	public static final String SCHEME_ZIP = "zip"; //$NON-NLS-1$

	@Override
	public IFileStore getStore(URI uri) {
		if (SCHEME_ZIP.equals(uri.getScheme())) {
			if (uri.getPath() == null) {
				// The entire file path is in the schemeSpecificPart
				String schemeSpecificPart = uri.getSchemeSpecificPart();
				// Extract the zip file URI part (before the !)
				String zipFileUriString = schemeSpecificPart.split("!")[0]; // everything before the ! //$NON-NLS-1$

				// Extract the path inside the zip (after the !)
				String pathInZip = schemeSpecificPart.contains("!") ? schemeSpecificPart.split("!")[1] : ""; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

				try {
					// Use the extracted zipFileUriString as the base URI
					URI zipFileUri = new URI(zipFileUriString);
					IPath path = IPath.fromOSString(pathInZip);
					return new ZipFileStore(EFS.getStore(zipFileUri), path);
				} catch (URISyntaxException e) {
					//ignore and fall through below
				} catch (CoreException e) {
					//ignore and fall through below
				}
			} else {
				IPath path = IPath.fromOSString(uri.getPath());
				try {
					return new ZipFileStore(EFS.getStore(new URI(uri.getQuery())), path);
				} catch (URISyntaxException e) {
					//ignore and fall through below
				} catch (CoreException e) {
					//ignore and fall through below
				}
			}

		}
		return EFS.getNullFileSystem().getStore(uri);
	}

	/**
	 * Returns the current OS.  This is equivalent to Platform.getOS(), but
	 * is tolerant of the platform runtime not being present.
	 */
	static String getOS() {
		return System.getProperty("osgi.os", ""); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Override
	public boolean canDelete() {
		return true;
	}

	@Override
	public boolean canWrite() {
		return true;
	}
}
