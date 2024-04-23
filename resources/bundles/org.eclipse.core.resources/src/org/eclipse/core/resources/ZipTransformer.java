package org.eclipse.core.resources;

import java.net.URI;
import java.net.URISyntaxException;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.URIUtil;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

/**
 * @since 3.21
 *
 */
public class ZipTransformer {
	public static void collapseZip(IFolder folder) throws URISyntaxException, CoreException {
		URI zipURI = new URI(folder.getLocationURI().getQuery());
		IFileStore parentStore = EFS.getStore(folder.getParent().getLocationURI());
		URI childURI = parentStore.getChild(folder.getName()).toURI();
		if (URIUtil.equals(zipURI, childURI)) {
			folder.delete(IResource.COLLAPSE, null);
			folder.getProject().refreshLocal(IResource.DEPTH_INFINITE, null);
		}
	}

	public static void expandZip(IFile file) throws URISyntaxException, CoreException {
		URI zipURI = new URI("zip", null, "/", file.getLocationURI().toString(), null); //$NON-NLS-1$ //$NON-NLS-2$
		IFolder link = file.getParent().getFolder(IPath.fromOSString(file.getName()));
		link.createLink(zipURI, IResource.REPLACE, null);
	
		// Roleback if Folder "link" is empty
		if (link.exists() && link.members().length == 0) {
			collapseZip(link);
		}
	}
}
