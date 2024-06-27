package org.eclipse.core.internal.events;

import java.net.URISyntaxException;
import org.eclipse.core.internal.resources.Folder;
import org.eclipse.core.internal.resources.IManager;
import org.eclipse.core.internal.resources.Workspace;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ZipFileTransformer;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * This listener aims to handle preparations needed before moving resources,
 * specifically for operations that may involve zip files represented as
 * folders.
 */
public class ZipMovePreparationListener implements IManager, ILifecycleListener {

	@Override
	public void handleEvent(LifecycleEvent event) {
		if (event.kind == LifecycleEvent.PRE_LINK_MOVE && event.resource.getType() == IResource.FOLDER) {
			Folder folder = (Folder) event.resource;
			String scheme = folder.getRawLocationURI().getScheme();
			if (!(scheme.contentEquals("zip"))) { //$NON-NLS-1$
				return;
			}
			try {
				ZipFileTransformer.closeZipFile(folder);
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void startup(Workspace workspace) {
		workspace.addLifecycleListener(this);
	}

	@Override
	public void shutdown(IProgressMonitor monitor) throws CoreException {
	}

	@Override
	public void startup(IProgressMonitor monitor) throws CoreException {
	}
}
