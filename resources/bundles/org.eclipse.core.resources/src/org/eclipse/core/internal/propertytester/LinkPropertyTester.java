/*******************************************************************************
 * Copyright (c) 2024 IBM Corporation and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.core.internal.propertytester;

import org.eclipse.core.resources.IFile;

/**
 *
 */
public class LinkPropertyTester extends ResourcePropertyTester {

	private static final String PROPERTY_IS_LINKED = "linked"; //$NON-NLS-1$

	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		if (PROPERTY_IS_LINKED.equals(property) && receiver instanceof IFile) {
			return ((IFile) receiver).isLinked();
		}
		return false;
	}

}
