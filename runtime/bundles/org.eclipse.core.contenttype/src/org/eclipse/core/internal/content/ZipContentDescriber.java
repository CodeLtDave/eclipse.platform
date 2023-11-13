/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.core.internal.content;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.content.IContentDescriber;
import org.eclipse.core.runtime.content.IContentDescription;

/**
 * Ein ContentDescriber für ZIP-Dateien. Erkennt ZIP-Dateien anhand ihrer
 * charakteristischen Signatur am Anfang der Datei (die ersten 4 Bytes).
 */
public class ZipContentDescriber implements IContentDescriber {

	// Die ersten 4 Bytes einer ZIP-Datei: 0x50, 0x4B, 0x03, 0x04
	private static final byte[] ZIP_SIGNATURE = { 0x50, 0x4B, 0x03, 0x04 };

	@Override
	public int describe(InputStream contents, IContentDescription description) throws IOException {
		byte[] buffer = new byte[ZIP_SIGNATURE.length];
		int readCount = contents.read(buffer);

		// Wenn die Anzahl der gelesenen Bytes nicht ausreicht oder die Signatur nicht
		// übereinstimmt,
		// ist die Datei kein gültiges ZIP-Archiv.
		if (readCount != ZIP_SIGNATURE.length || !Arrays.equals(ZIP_SIGNATURE, buffer)) {
			return INVALID;
		}

		// Die Datei ist ein gültiges ZIP-Archiv.
		return VALID;
	}

	@Override
	public QualifiedName[] getSupportedOptions() {
		// Keine speziellen Optionen werden unterstützt.
		return new QualifiedName[0];
	}
}
