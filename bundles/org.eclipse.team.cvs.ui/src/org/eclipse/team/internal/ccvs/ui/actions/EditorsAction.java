/*******************************************************************************
 * Copyright (c) 2003 CSC SoftwareConsult GmbH & Co. OHG, Germany and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 * 	CSC - Intial implementation
 *******************************************************************************/
package org.eclipse.team.internal.ccvs.ui.actions;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.team.core.Team;
import org.eclipse.team.internal.ccvs.core.*;
import org.eclipse.team.internal.ccvs.ui.*;
import org.eclipse.team.internal.ccvs.ui.Policy;
import org.eclipse.team.internal.ccvs.ui.actions.WorkspaceAction.IProviderAction;

/**
 * This Action gets the <code>EditorsInfo[]</code>
 * It is used by the <code>ShowEditorAction</code> 
 * and <code>ShowEditorAction</code>.
 * 
 * @author <a href="mailto:gregor.kohlwes@csc.com,kohlwes@gmx.net">Gregor
 * Kohlwes</a>
 * 
 */
public class EditorsAction implements IProviderAction, IRunnableWithProgress {
	EditorsInfo[] f_editorsInfo = new EditorsInfo[0];
	CVSTeamProvider f_provider;
	IResource[] f_resources;

	public EditorsAction() {
	}
	
	public EditorsAction(CVSTeamProvider provider, IResource[] resources) {
		f_provider = provider;
		f_resources = resources;
	}
	/**
	 * @see org.eclipse.team.internal.ccvs.ui.actions.WorkspaceAction.IProviderAction#execute(org.eclipse.team.internal.ccvs.core.CVSTeamProvider, org.eclipse.core.resources.IResource, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public IStatus execute(CVSTeamProvider provider, IResource[] resources, IProgressMonitor monitor) throws CVSException {
		f_editorsInfo = provider.editors(resources, monitor);
		return Team.OK_STATUS;
	}
	
	public boolean promptToEdit(Shell shell) {
		if (!isEmpty()) {
			final EditorsDialog view = new EditorsDialog(shell, f_editorsInfo);
			// Open the dialog using a sync exec (there are no guarentees that we
			// were called from the UI thread
			CVSUIPlugin.openDialog(shell, new CVSUIPlugin.IOpenableInShell() {
				public void open(Shell shell) {
					view.open();
				}
			}, CVSUIPlugin.PERFORM_SYNC_EXEC);
			return (view.getReturnCode() == EditorsDialog.OK);
		}
		return true;
	}

	/**
	 * Contact the server to determine if there are any editors on the associatd files.
	 * 
	 * @see org.eclipse.jface.operation.IRunnableWithProgress#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
		if (f_provider == null || f_resources == null) {
			throw new InvocationTargetException(new RuntimeException(Policy.bind("EditorsAction.classNotInitialized", this.getClass().getName()))); //$NON-NLS-1$
		}
		try {
			execute(f_provider,f_resources,monitor);
		} catch (CVSException e) {
			throw new InvocationTargetException(e);
		}
	}

	/**
	 * Returns the f_editorsInfo.
	 * @return EditorsInfo[]
	 */
	public EditorsInfo[] getEditorsInfo() {
		return f_editorsInfo;
	}

	/**
	 * Indicates whether there are editors of any of the associated files.
	 * The <code>run(IProgressMonitor)</code> must be invoked first to 
	 * fetch any editors from the server.
	 * @return boolean
	 */
	public boolean isEmpty() {
		return f_editorsInfo.length == 0;
	}

}
