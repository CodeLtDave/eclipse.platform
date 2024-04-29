package org.eclipse.core.resources;

import java.net.URI;
import java.net.URISyntaxException;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.URIUtil;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * Utility class for collapsing and expanding archive files.
 *
 * @since 3.21
 */
public class ArchiveTransformer {

	/**
	 * Collapses an expanded archive file represented as a linked folder in the
	 * workspace. After collapsing the archive file in its unexpanded state is shown
	 * in the workspace.
	 *
	 * This method can only be called when the archive file is local. Otherwise a
	 * CoreException is thrown.
	 *
	 * @param folder The folder representing the archive file to collapse.
	 *
	 */
	public static void collapseArchive(IFolder folder) throws URISyntaxException, CoreException {
		URI zipURI = new URI(folder.getLocationURI().getQuery());
		IFileStore parentStore = EFS.getStore(folder.getParent().getLocationURI());
		URI childURI = parentStore.getChild(folder.getName()).toURI();
		if (URIUtil.equals(zipURI, childURI)) {
			folder.delete(IResource.COLLAPSE, null);
			folder.getProject().refreshLocal(IResource.DEPTH_INFINITE, null);
		} else {
			throw new CoreException(new Status(IStatus.ERROR, ResourcesPlugin.PI_RESOURCES,
					"Collapsing of folder " + folder.getName() //$NON-NLS-1$
							+ " failed because the corresponding archive file is not local.")); //$NON-NLS-1$
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
	 * This method prevents expanding linked archive files. Archive files must be
	 * local to be expanded. Otherwise a CoreException is thrown.
	 *
	 * @param file    The file representing the archive file to expand.
	 * @param monitor monitor indicating the completion progress
	 *
	 */
	public static void expandArchive(IFile file, IProgressMonitor monitor) throws URISyntaxException, CoreException {
		if (file.isLinked()) {
			throw new CoreException(new Status(IStatus.ERROR, ResourcesPlugin.PI_RESOURCES,
					"The file " + file.getName() + " is a linked resource and thus can not be expanded")); //$NON-NLS-1$ //$NON-NLS-2$
		}
		URI zipURI = new URI("zip", null, "/", file.getLocationURI().toString(), null); //$NON-NLS-1$ //$NON-NLS-2$
		IFolder link = file.getParent().getFolder(IPath.fromOSString(file.getName()));
		monitor.worked(1);
		link.createLink(zipURI, IResource.REPLACE, null);
		monitor.worked(3);

		// Roleback if Folder "link" is empty
		if (link.exists() && link.members().length == 0) {
			collapseArchive(link);
			throw new CoreException(new Status(IStatus.ERROR, ResourcesPlugin.PI_RESOURCES,
					"Archive could not be expanded or has no children")); //$NON-NLS-1$
		}
	}
}
