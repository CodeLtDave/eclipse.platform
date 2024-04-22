package org.eclipse.core.filesystem.zip;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;

/**
 * Utility class to determine if a file is an archive based on file header information.
 *
 * @since 1.11
 */
public class ZipFileUtil {

    private static final Set<Integer> ZIP_MAGIC_NUMBERS = new HashSet<>();

    static {
        // Common ZIP file headers
        ZIP_MAGIC_NUMBERS.add(0x504B0304); // Standard ZIP file
        ZIP_MAGIC_NUMBERS.add(0x504B0506); // End of central directory signature (empty ZIP)
        ZIP_MAGIC_NUMBERS.add(0x504B0708); // Spanned ZIP
    }

	public static boolean isArchive(IPath workspaceFolderPath) {
		// Get the actual file system path for the workspace folder
		IPath workspaceRoot = new Path(Platform.getInstanceLocation().getURL().getPath());
		IPath filePath = workspaceRoot.append(workspaceFolderPath);
		File file = filePath.toFile();

		// Check if this file is actually an archive
		return checkZipHeaders(file);
	}

	public static boolean checkZipHeaders(File file) {
		if (!file.exists() || !file.isFile() || !file.canRead()) {
			return false;
		}

		try (FileInputStream fis = new FileInputStream(file)) {
			byte[] bytes = new byte[4];
			if (fis.read(bytes) == bytes.length) {
				ByteBuffer buffer = ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN);
				int header = buffer.getInt();

				return ZIP_MAGIC_NUMBERS.contains(header);
			}
		} catch (IOException e) {
			e.printStackTrace(); // Log or handle the exception as needed
		}
		return false; // Ensure this is the only place that returns false when no other conditions are met
	}
}
