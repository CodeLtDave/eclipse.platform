package org.eclipse.debug.internal.ui.actions;

/*
 * (c) Copyright IBM Corp. 2000, 2001.
 * All Rights Reserved.
 */
 
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.internal.ui.DebugPluginImages;
import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.eclipse.debug.internal.ui.IInternalDebugUIConstants;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;

/**
 * Terminates all launches.
 */
public class TerminateAllAction extends AbstractListenerActionDelegate {
	
	public TerminateAllAction() {
	}
	
	protected void doAction(Object element) {
	}
	
	protected void update() {
		ILaunchManager lManager= DebugPlugin.getDefault().getLaunchManager();
		ILaunch[] launches= lManager.getLaunches();
		for (int i= 0; i< launches.length; i++) {
			ILaunch launch= launches[i];
			if (!launch.isTerminated()) {
				getAction().setEnabled(true);
				return;
			}
		}
		getAction().setEnabled(false);
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		setAction(action);
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		ILaunchManager lManager= DebugPlugin.getDefault().getLaunchManager();
		ILaunch[] launches= lManager.getLaunches();
		MultiStatus ms = new MultiStatus(DebugPlugin.getDefault().getDescriptor().getUniqueIdentifier(), 
			DebugException.REQUEST_FAILED, ActionMessages.getString("TerminateAllAction.Terminate_all_failed_3"), null); //$NON-NLS-1$
		for (int i= 0; i < launches.length; i++) {
			ILaunch launch= (ILaunch) launches[i];
			if (!launch.isTerminated()) {
				try {
					launch.terminate();
				} catch (DebugException de) {
					ms.merge(de.getStatus());
				}
			}
		}
		if (!ms.isOK()) {
			DebugUIPlugin.errorDialog(DebugUIPlugin.getActiveWorkbenchWindow().getShell(), ActionMessages.getString("TerminateAllAction.Terminate_all_2"),ActionMessages.getString("TerminateAllAction.Exceptions_occurred_attempting_to_terminate_all._5") , ms); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	/**
	 * @see AbstractDebugActionDelegate#isEnabledFor(Object)
	 */
	protected boolean isEnabledFor(Object element) {
		return true;
	}

	/**
	 * @see AbstractDebugActionDelegate#setActionImages(IAction)
	 */
	protected void setActionImages(IAction action) {
		action.setHoverImageDescriptor(DebugPluginImages.getImageDescriptor(IDebugUIConstants.IMG_LCL_TERMINATE_ALL));
		action.setDisabledImageDescriptor(DebugPluginImages.getImageDescriptor(IInternalDebugUIConstants.IMG_DLCL_TERMINATE_ALL));
		action.setImageDescriptor(DebugPluginImages.getImageDescriptor(IInternalDebugUIConstants.IMG_ELCL_TERMINATE_ALL));
	}

	protected void doHandleDebugEvent(DebugEvent event) {
		switch (event.getKind()) {
			case DebugEvent.TERMINATE :
				update();
				break;
			case DebugEvent.CREATE :
				update();
				break;
		}
	}		
}