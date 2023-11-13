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

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFolderArchive;
import org.eclipse.core.runtime.IPath;

public class FolderArchive extends Container implements IFolderArchive {
	protected FolderArchive(IPath path, Workspace container) {
		super(path, container);
	}

	@Override
	public String getDefaultCharset(boolean checkImplicit) {
		return null;
	}

	@Override
	public int getType() {
		return FOLDER_ARCHIVE;
	}

	@Override
	public IFileStore getStore() {
		return super.getStore();
	}
}
