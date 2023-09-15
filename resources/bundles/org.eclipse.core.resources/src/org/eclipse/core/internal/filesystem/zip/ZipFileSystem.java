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

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.provider.FileSystem;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

/**
 * @since 3.20
 *
 */
public class ZipFileSystem extends FileSystem {
	/**
	 * Scheme constant (value "zip") indicating the zip file system scheme.
	 */
	public static final String SCHEME_ZIP = "zip"; //$NON-NLS-1$

	@Override
	public IFileStore getStore(URI uri) {
		if (SCHEME_ZIP.equals(uri.getScheme())) {
			IPath path = IPath.fromOSString(uri.getPath());
			String pathString = uri.getPath();
			if (File.separatorChar != '/')
				pathString = pathString.replace(File.separatorChar, '/');
			final int length = pathString.length();
			StringBuilder pathBuf = new StringBuilder(length + 1);
			//mark if path is relative
			if (length > 0 && (pathString.charAt(0) != '/')) {
				pathBuf.append('/');
			}
			//additional double-slash for UNC paths to distinguish from host separator
			if (pathString.startsWith("//")) //$NON-NLS-1$
				pathBuf.append('/').append('/');
			pathBuf.append(pathString);
			String scheme = EFS.SCHEME_FILE;
			try {
				return new ZipFileStore(EFS.getStore(new URI(scheme, null, pathBuf.toString(), null)), path);
			} catch (URISyntaxException e) {
				//ignore and fall through below
			} catch (CoreException e) {
				//ignore and fall through below
			}
		}
		return EFS.getNullFileSystem().getStore(uri);
	}
}
