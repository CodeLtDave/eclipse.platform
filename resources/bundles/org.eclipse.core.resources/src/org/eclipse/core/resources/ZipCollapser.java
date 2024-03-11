package org.eclipse.core.resources;

import java.net.URI;
import java.net.URISyntaxException;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.URIUtil;
import org.eclipse.core.runtime.CoreException;

/**
 * @since 3.21
 *
 */
public class ZipCollapser {
	public static void collapseZip(IFolder folder) throws URISyntaxException, CoreException {
			URI zipURI = new URI(folder.getLocationURI().getQuery());
			IFileStore parentStore = EFS.getStore(folder.getParent().getLocationURI());
			URI childURI = parentStore.getChild(folder.getName()).toURI();
			if (URIUtil.equals(zipURI, childURI)) {
				folder.delete(IResource.COLLAPSE, null);
				folder.getProject().refreshLocal(IResource.DEPTH_INFINITE, null);
			}
	}
}
