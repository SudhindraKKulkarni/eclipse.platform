/*******************************************************************************
 * Copyright (c) 2003 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v0.5
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v05.html
 * 
 * Contributors:
 * IBM - Initial implementation
 ******************************************************************************/
package org.eclipse.team.internal.ccvs.ui.repo;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.team.core.TeamException;
import org.eclipse.team.internal.ccvs.core.CVSException;
import org.eclipse.team.internal.ccvs.core.CVSTag;
import org.eclipse.team.internal.ccvs.core.ICVSRemoteFolder;
import org.eclipse.team.internal.ccvs.ui.CVSUIPlugin;
import org.eclipse.team.internal.ccvs.ui.Policy;
import org.eclipse.team.internal.ccvs.ui.actions.CVSAction;

/**
 * Action to add a root remote folder to a branch
 */
public class AddToBranchAction extends CVSAction {

	IInputValidator validator = new IInputValidator() {
		public String isValid(String newText) {
			IStatus status = CVSTag.validateTagName(newText);
			if (status.isOK()) return null;
			return status.getMessage();
		}
	};
	
	/**
	 * @see org.eclipse.team.internal.ccvs.ui.actions.CVSAction#execute(org.eclipse.jface.action.IAction)
	 */
	protected void execute(IAction action) throws InvocationTargetException, InterruptedException {
		run(new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException {
				final ICVSRemoteFolder folder = getSelectedRootFolder();
				if (folder == null) return;
				Shell shell = getShell();
				final CVSException[] exception = new CVSException[] { null };
				shell.getDisplay().syncExec(new Runnable() {
					public void run() {
						InputDialog dialog = new InputDialog(getShell(), Policy.bind("AddToBranchAction.enterTag"), Policy.bind("AddToBranchAction.enterTagLong"), null, validator); //$NON-NLS-1$ //$NON-NLS-2$
						if (dialog.open() == InputDialog.OK) {
							CVSTag tag = new CVSTag(dialog.getValue(), CVSTag.BRANCH);
							try {
								CVSUIPlugin.getPlugin().getRepositoryManager().addTags(folder, new CVSTag[] {tag});
							} catch (CVSException e) {
								exception[0] = e;
							}
						}
					}
				});
				if (exception[0] != null)
					throw new InvocationTargetException(exception[0]);
			}
		}, false, PROGRESS_BUSYCURSOR); //$NON-NLS-1$
	}

	/**
	 * @see org.eclipse.team.internal.ui.actions.TeamAction#isEnabled()
	 */
	protected boolean isEnabled() throws TeamException {
		return getSelectedRootFolder() != null;
	}

	protected ICVSRemoteFolder getSelectedRootFolder() {
		ICVSRemoteFolder[] folders = getSelectedRemoteFolders();
		ICVSRemoteFolder selectedFolder = null;
		for (int i = 0; i < folders.length; i++) {
			ICVSRemoteFolder folder = folders[i];
			if (folder.isDefinedModule() || new Path(folder.getRepositoryRelativePath()).segmentCount()==1) {
				// only return a folder if one valid one is selected.
				if (selectedFolder != null) return null;
				selectedFolder = folder;
			}
		}
		return selectedFolder;
	}
}
