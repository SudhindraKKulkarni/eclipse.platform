/*******************************************************************************
 * Copyright (c) 2009, 2013 Wind River Systems and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Wind River Systems - initial API and implementation
 *     IBM Corporation - bug fixing
 *******************************************************************************/
package org.eclipse.debug.tests.viewer.model;

import org.eclipse.debug.internal.ui.viewers.model.IInternalTreeModelViewer;
import org.eclipse.debug.internal.ui.viewers.model.provisional.PresentationContext;
import org.eclipse.debug.internal.ui.viewers.model.provisional.VirtualTreeModelViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * Tests which verify the operation of the virtual viewer in the lazy mode.
 * Note: the virtual viewer doesn't support lazy mode yet, so this class
 * is really just a big place holder.
 *
 *  @since 3.6
 */
public class VirtualViewerLazyModeTests extends AbstractViewerModelTest {

    public VirtualViewerLazyModeTests(String name) {
        super(name);
    }

	@Override
	protected TestModelUpdatesListener createListener(IInternalTreeModelViewer viewer) {
		return new TestModelUpdatesListener(viewer, false, false);
	}

	@Override
	protected IInternalTreeModelViewer createViewer(Display display, Shell shell) {
		return new VirtualTreeModelViewer(display, SWT.VIRTUAL, new PresentationContext("TestViewer")); //$NON-NLS-1$
    }

    public void test() {
        // TODO
    }
}
