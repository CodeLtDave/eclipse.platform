/*******************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.debug.internal.core.sourcelookup.containers;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.debug.internal.core.sourcelookup.ISourceContainer;
import org.eclipse.debug.internal.core.sourcelookup.ISourceContainerType;
import org.eclipse.debug.internal.core.sourcelookup.SourceLookupMessages;
import org.eclipse.debug.internal.core.sourcelookup.SourceLookupUtils;

/**
 * All projects in the workspace.
 * 
 * @since 3.0
 */
public class WorkspaceSourceContainer extends CompositeSourceContainer {
	
	public WorkspaceSourceContainer() {
	}	

	/* (non-Javadoc)
	 * @see org.eclipse.debug.internal.core.sourcelookup.ISourceContainer#getName()
	 */
	public String getName() {
		return SourceLookupMessages.getString("WorkspaceSourceContainer.0"); //$NON-NLS-1$
	}
		
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		return obj instanceof WorkspaceSourceContainer;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */	
	public int hashCode() {
		return ResourcesPlugin.getWorkspace().hashCode();
	}
			
	/* (non-Javadoc)
	 * @see org.eclipse.debug.internal.core.sourcelookup.ISourceContainer#getType()
	 */
	public ISourceContainerType getType() {
		return SourceLookupUtils.getSourceContainerType(WorkspaceSourceContainerType.TYPE_ID);
	}
	/* (non-Javadoc)
	 * @see org.eclipse.debug.internal.core.sourcelookup.ISourceContainer#getSourceContainers()
	 */
	public ISourceContainer[] getSourceContainers() {
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		ISourceContainer[] containers = new ISourceContainer[projects.length];
		for (int i = 0; i < projects.length; i++) {
			containers[i] = new ProjectSourceContainer(projects[i], false);
		}
		return containers;
	}

}
