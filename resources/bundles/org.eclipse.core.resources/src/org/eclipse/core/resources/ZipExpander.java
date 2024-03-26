package org.eclipse.core.resources;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.URIUtil;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

/**
 * @since 3.21
 *
 */
public class ZipExpander {
	public static void expandZip(IFile file) throws URISyntaxException, CoreException, IOException {
		URI zipURI = new URI("zip", null, "/", file.getLocationURI().toString(), null); //$NON-NLS-1$ //$NON-NLS-2$
		Map<String, Object> env = new HashMap<>();
		env.put("create", "false"); //$NON-NLS-1$ //$NON-NLS-2$
		if (file.getLocation() != null) {
		IFileStore fileStore = EFS.getStore(URIUtil.toURI(file.getLocation()));
		URI jarUri = new URI("jar:" + fileStore.toURI().toString() + "!/"); //$NON-NLS-1$ //$NON-NLS-2$
		// Try creating file System to catch errors before actual execution
		try (FileSystem __ = FileSystems.newFileSystem(jarUri, env)) {
			IFolder link = file.getParent().getFolder(IPath.fromOSString(file.getName()));
			link.createLink(zipURI, IResource.REPLACE, null);
		}
	}
	}
}
