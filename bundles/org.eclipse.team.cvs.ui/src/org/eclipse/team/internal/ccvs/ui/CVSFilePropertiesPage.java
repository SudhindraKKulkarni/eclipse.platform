/*******************************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.team.internal.ccvs.ui;


import java.text.DateFormat;
import java.util.Date;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.team.core.TeamException;
import org.eclipse.team.internal.ccvs.core.CVSTag;
import org.eclipse.team.internal.ccvs.core.ICVSFile;
import org.eclipse.team.internal.ccvs.core.resources.CVSWorkspaceRoot;
import org.eclipse.team.internal.ccvs.core.syncinfo.ResourceSyncInfo;
import org.eclipse.team.internal.ccvs.core.util.Util;
import org.eclipse.ui.help.WorkbenchHelp;

public class CVSFilePropertiesPage extends CVSPropertiesPage {
	IFile file;

	/*
	 * @see PreferencesPage#createContents
	 */
	protected Control createContents(Composite parent) {
		initialize();
		noDefaultAndApplyButton();
		Composite composite = new Composite(parent, SWT.NULL);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);
		
		try {
			ICVSFile cvsResource = CVSWorkspaceRoot.getCVSFileFor(file);
			if (!cvsResource.isManaged()) {
				if (cvsResource.isIgnored()) {
					createLabel(composite, CVSUIMessages.CVSFilePropertiesPage_ignored); //$NON-NLS-1$
				} else {
					createLabel(composite, CVSUIMessages.CVSFilePropertiesPage_notManaged); //$NON-NLS-1$
				}
				createLabel(composite, ""); //$NON-NLS-1$
				return composite;
			}
			ResourceSyncInfo syncInfo = cvsResource.getSyncInfo();
			

			
			if (syncInfo.isAdded()) {
				createLabel(composite, CVSUIMessages.CVSFilePropertiesPage_isAdded, 2); //$NON-NLS-1$
			} else {
				// Base
				createLabel(composite, CVSUIMessages.CVSFilePropertiesPage_baseRevision); //$NON-NLS-1$
				createLabel(composite, syncInfo.getRevision());
				Date baseTime = syncInfo.getTimeStamp();
				if (baseTime != null) {
					createLabel(composite, CVSUIMessages.CVSFilePropertiesPage_baseTimestamp); //$NON-NLS-1$
					createLabel(composite, DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL).format(syncInfo.getTimeStamp()));
				}
				
				// Modified
				createLabel(composite, CVSUIMessages.CVSFilePropertiesPage_modified); //$NON-NLS-1$
				createLabel(composite, cvsResource.isModified(null) ? CVSUIMessages.yes : CVSUIMessages.no); //$NON-NLS-1$ //$NON-NLS-2$
			}
			
			// Keyword Mode
			createLabel(composite, CVSUIMessages.CVSFilePropertiesPage_keywordMode); //$NON-NLS-1$
			createLabel(composite, syncInfo.getKeywordMode().getLongDisplayText());
			
			// Tag
			createLabel(composite, CVSUIMessages.CVSFilePropertiesPage_tag); //$NON-NLS-1$
			CVSTag tag = Util.getAccurateFileTag(cvsResource);
			createLabel(composite, getTagLabel(tag));
		} catch (TeamException e) {
			// Display error text
			createLabel(composite, CVSUIMessages.CVSFilePropertiesPage_error); //$NON-NLS-1$
			createLabel(composite, ""); //$NON-NLS-1$
		}
		WorkbenchHelp.setHelp(getControl(), IHelpContextIds.FILE_PROPERTY_PAGE);
        Dialog.applyDialogFont(parent);
		return composite;
	}
	/**
	 * Utility method that creates a label instance
	 * and sets the default layout data.
	 *
	 * @param parent  the parent for the new label
	 * @param text  the text for the new label
	 * @return the new label
	 */
	protected Label createLabel(Composite parent, String text, int span) {
		Label label = new Label(parent, SWT.LEFT);
		label.setText(text);
		GridData data = new GridData();
		data.horizontalSpan = span;
		data.horizontalAlignment = GridData.FILL;
		label.setLayoutData(data);
		return label;
	}
	protected Label createLabel(Composite parent, String text) {
		return createLabel(parent, text, 1);
	}
	/**
	 * Initializes the page
	 */
	private void initialize() {
		// Get the file that is the source of this property page
		file = null;
		IAdaptable element = getElement();
		if (element instanceof IFile) {
			file = (IFile)element;
		} else {
			Object adapter = element.getAdapter(IFile.class);
			if (adapter instanceof IFile) {
				file = (IFile)adapter;
			}
		}
	}
}

