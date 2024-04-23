package org.eclipse.core.resources;

import java.net.URI;
import java.net.URISyntaxException;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.URIUtil;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

/**
 * Utility class for collapsing and expanding archive files.
 *
 * @since 3.21
 */
public class ZipTransformer {

	/**
	 * Collapses an expanded archive file represented as a linked folder in the
	 * workspace. After collapsing the archive file in its unexpanded state is shown
	 * in the workspace.
	 *
	 * @param folder The folder representing the archive file to collapse.
	 *
	 */
	public static void collapseZip(IFolder folder) throws URISyntaxException, CoreException {
		URI zipURI = new URI(folder.getLocationURI().getQuery());
		IFileStore parentStore = EFS.getStore(folder.getParent().getLocationURI());
		URI childURI = parentStore.getChild(folder.getName()).toURI();
		if (URIUtil.equals(zipURI, childURI)) {
			folder.delete(IResource.COLLAPSE, null);
			folder.getProject().refreshLocal(IResource.DEPTH_INFINITE, null);
		}
	}

	/**
	 * Expands an archive file represented by a file into a linked folder. In the
	 * expanded state the linked folder allows reading and manipulating the archives
	 * children in the workspace and on the filesystem. If the folder has no
	 * children after expanding, then it is collapsed immediately after. In this
	 * case it could be that there should be no children, so no need for expanding
	 * or an error occured that prevented the children from being loaded.
	 *
	 * @param file The file representing the archive file to expand.
	 *
	 */
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
