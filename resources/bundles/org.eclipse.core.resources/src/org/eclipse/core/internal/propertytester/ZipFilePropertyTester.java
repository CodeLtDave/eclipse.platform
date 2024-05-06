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
package org.eclipse.core.internal.propertytester;

import org.eclipse.core.resources.IFile;

/**
 *
 */
public class ZipFilePropertyTester extends ResourcePropertyTester {

	private static final String PROPERTY_IS_ZIP_FILE = "zipFile"; //$NON-NLS-1$

	private enum ZipFileExtensions {
		ZIP("zip"), //$NON-NLS-1$
		JAR("jar"); //$NON-NLS-1$

		private final String value;

		ZipFileExtensions(String value) {
			this.value = value;
		}
	}

	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		if (!(receiver instanceof IFile))
			return false;

		IFile file = (IFile) receiver;

		if (property.equals(PROPERTY_IS_ZIP_FILE)) {
			String fileExtension = file.getFileExtension();
			boolean isZipFile = false;

			for (ZipFileExtensions allowedExtension : ZipFileExtensions.values()) {
				if (fileExtension.equals(allowedExtension.value)) {
					isZipFile = true;
					break;
				}
			}

			if (!file.isLinked() && isZipFile)
				return true;
		}

		return false;
	}

}
