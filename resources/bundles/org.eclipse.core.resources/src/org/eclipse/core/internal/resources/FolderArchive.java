/*******************************************************************************
 * Copyright (c) 2000, 2015 IBM Corporation and others.
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
 *     Serge Beauchamp (Freescale Semiconductor) - [229633] Group and Project Path Variable Support
 *******************************************************************************/
package org.eclipse.core.internal.resources;

import org.eclipse.core.resources.IFolderArchive;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;

public class FolderArchive extends Container implements IFolderArchive {
	protected FolderArchive(IPath path, Workspace container) {
		super(path, container);
	}

	@Override
	public String getDefaultCharset(boolean checkImplicit) throws CoreException {
		return null;
	}

	@Override
	public void create(boolean force, boolean local, IProgressMonitor monitor) throws CoreException {
	}

	@Override
	public void create(int updateFlags, boolean local, IProgressMonitor monitor) throws CoreException {
	}

	@Override
	public int getType() {
		return FOLDER_ARCHIVE;
	}
}
