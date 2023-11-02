/*******************************************************************************
 * Copyright (c) 2023 IBM Corporation and others.
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
package org.eclipse.core.internal.resources;

import java.util.HashMap;

/**
 *
 */
public class FolderArchiveInfo extends ResourceInfo {

	/** The description of this folder archive. */
	protected ModelObject description;

	/** The attributes associated with this folder archive, if any. */
	protected HashMap<String, Object> attributes;

	/** Default constructor. */
	public FolderArchiveInfo() {
		super();
	}

	/**
	 * Discards cached attributes for this folder archive when they become stale.
	 */
	public synchronized void discardAttributes() {
		attributes = null;
	}

	/**
	 * Returns the description associated with this folder archive. May return null
	 * if no description has been set.
	 */
	public ModelObject getDescription() {
		return description;
	}

	/**
	 * Returns the attribute associated with the given ID for this folder archive.
	 * May return null if the attribute hasn't been set.
	 */
	public Object getAttribute(String attributeId) {
		HashMap<String, Object> temp = attributes;
		if (temp == null)
			return null;
		return temp.get(attributeId);
	}

	@Override
	public int getType() {
		return 16;
	}

	/**
	 * Sets the description for this folder archive.
	 */
	public void setDescription(ModelObject value) {
		description = value;
	}

	/**
	 * Sets or removes an attribute for this folder archive. If the value is null,
	 * the attribute with the given ID is removed. Otherwise, it's set to the given
	 * value.
	 */
	@SuppressWarnings({ "unchecked" })
	public synchronized void setAttribute(String attributeId, Object value) {
		if (value == null) {
			if (attributes == null)
				return;
			HashMap<String, Object> temp = (HashMap<String, Object>) attributes.clone();
			temp.remove(attributeId);
			if (temp.isEmpty())
				attributes = null;
			else
				attributes = temp;
		} else {
			HashMap<String, Object> temp = attributes;
			if (temp == null)
				temp = new HashMap<>(5);
			else
				temp = (HashMap<String, Object>) attributes.clone();
			temp.put(attributeId, value);
			attributes = temp;
		}
	}
}
