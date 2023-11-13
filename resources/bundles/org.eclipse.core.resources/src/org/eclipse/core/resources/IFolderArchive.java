/*******************************************************************************
 *  Copyright (c) 2000, 2010 IBM Corporation and others.
 *
 *  This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License 2.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-2.0/
 *
 *  SPDX-License-Identifier: EPL-2.0
 *
 *  Contributors:
 *     IBM Corporation - initial API and implementation
 *     Serge Beauchamp (Freescale Semiconductor) - [229633] Group Support
 *******************************************************************************/
package org.eclipse.core.resources;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;

/**
 * Folders may be leaf or non-leaf resources and may contain files and/or other
 * folders. A folder resource is stored as a directory in the local file system.
 * <p>
 * Folders, like other resource types, may exist in the workspace but not be
 * local; non-local folder resources serve as place-holders for folders whose
 * properties have not yet been fetched from a repository.
 * </p>
 * <p>
 * Folders implement the <code>IAdaptable</code> interface; extensions are
 * managed by the platform's adapter manager.
 * </p>
 *
 * @see Platform#getAdapterManager()
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 * @since 3.20
 */
public interface IFolderArchive extends IContainer, IAdaptable {
}
