package org.eclipse.core.runtime.content;

import java.io.IOException;
import java.io.InputStream;
import org.eclipse.core.runtime.QualifiedName;

/**
 * @since 3.9
 */
public class ZipContentDescriber implements IContentDescriber {

	private static final byte[] ZIP_MAGIC = { 'P', 'K', 0x03, 0x04 };

	@Override
	public int describe(InputStream contents, IContentDescription description) throws IOException {
		byte[] buffer = new byte[4];
		int bytesRead = contents.read(buffer);
		if (bytesRead == 4 && matches(buffer, ZIP_MAGIC)) {
			return VALID;
		}
		return INDETERMINATE;
	}

	private boolean matches(byte[] buffer, byte[] magic) {
		for (int i = 0; i < magic.length; i++) {
			if (buffer[i] != magic[i]) {
				return false;
			}
		}
		return true;
	}

	@Override
	public QualifiedName[] getSupportedOptions() {
		// In this case, we are not supporting any specific options
		return new QualifiedName[0];
	}

	public int describe(byte[] contents, IContentDescription description) throws IOException {
		if (contents.length >= 4 && matches(contents, ZIP_MAGIC)) {
			return VALID;
		}
		return INDETERMINATE;
	}
}