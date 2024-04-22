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
package org.eclipse.core.filesystem.zip;

import java.util.Arrays;
import org.eclipse.core.runtime.IPath;

/**
 * @since 1.11
 *
 */
public class ZipFileUtil {
	public static boolean isArchive(IPath archiveFilePath) {
			return Arrays.stream(archiveFilePath.segments()).anyMatch(segment -> segment.contains("zip") || segment.contains("jar")); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
