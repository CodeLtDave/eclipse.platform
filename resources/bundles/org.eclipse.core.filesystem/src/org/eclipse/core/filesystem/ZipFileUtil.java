package org.eclipse.core.filesystem;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.core.internal.filesystem.zip.ZipFileStore;

/**
 * Utility class to determine if a file is an archive based on file header information.
 * This class checks for known file signatures to identify if a given file is a ZIP archive
 * or a format based on ZIP, such as EPUB, JAR, ODF, and OOXML.
 *
 * @since 1.11
 */
public class ZipFileUtil {

    private static final Set<Integer> ARCHIVE_FILE_SIGNATURES = new HashSet<>();

    static {
		// Initializes known archive file signatures from Wikipedia's list of file signatures 
		// (https://en.wikipedia.org/wiki/List_of_file_signatures)
        ARCHIVE_FILE_SIGNATURES.add(0x504B0304); // Standard ZIP file
		ARCHIVE_FILE_SIGNATURES.add(0x504B0506); // Empty archive
		ARCHIVE_FILE_SIGNATURES.add(0x504B0708); // Spanned archive
    }

	/**
	 * Determines if the given {@link IFileStore} represents an open ZIP file.
	 * This can be used to check if operations on a ZIP file should be allowed or handled differently.
	 *
	 * @param store The file store to check.
	 * @return true if the store is an instance of {@link ZipFileStore}, false otherwise.
	 */
	public static boolean isInsideOpenZipFile(IFileStore store) {
		return store instanceof ZipFileStore;
	}

	/**
	 * Checks if the provided {@link InputStream} represents a ZIP archive
	 * by reading its first four bytes and comparing them against known ZIP file signatures.
	 * This method throws {@link IOException} if the file signature does not match any known ZIP archive signatures.
	 *
	 * @param fis The {@link InputStream} of the file to check.
	 * @throws IOException If the file signature does not match known ZIP archive signatures
	 *                     or an I/O error occurs during reading from the stream.
	 */
	public static void checkFileForZipHeader(InputStream fis) throws IOException {
		byte[] bytes = new byte[4];
		if (fis.read(bytes) == bytes.length) {
			ByteBuffer buffer = ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN);
			int header = buffer.getInt();

			if (!ARCHIVE_FILE_SIGNATURES.contains(header)) {
				throw new IOException("Invalid archive file signature."); //$NON-NLS-1$
			}
		}
	}
}
