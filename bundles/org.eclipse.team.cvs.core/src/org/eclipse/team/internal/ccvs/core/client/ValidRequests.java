/*******************************************************************************
 * Copyright (c) 2002 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v0.5
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v05.html
 * 
 * Contributors:
 * IBM - Initial API and implementation
 ******************************************************************************/
package org.eclipse.team.internal.ccvs.core.client;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.team.internal.ccvs.core.CVSException;
import org.eclipse.team.internal.ccvs.core.CVSStatus;
import org.eclipse.team.internal.ccvs.core.ICVSFolder;
import org.eclipse.team.internal.ccvs.core.client.listeners.ICommandOutputListener;

class ValidRequests extends Request {
		
	protected ValidRequests() { }
	
	protected String getRequestId() {
		return "valid-requests"; //$NON-NLS-1$
	}

	public IStatus execute(Session session, IProgressMonitor monitor) throws CVSException {
		return executeRequest(session, Command.DEFAULT_OUTPUT_LISTENER, monitor);
	}
}
