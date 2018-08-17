/**********************************************************************
 * Copyright (c) 2003, 2004 Geoff Longman and others.
 *
 *   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * Geoff Longman - Initial API and implementation
 * IBM - Tightening integration with existing Platform
 **********************************************************************/
package org.eclipse.core.tools.runtime;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.ui.views.properties.*;

/**
 * A read-only IPropertySource for Marker attributes.
 */
public class ContentTypePropertySource implements IPropertySource, IAdaptable {
	final static IPropertyDescriptor UNIQUE_ID = new TextPropertyDescriptor("id", "Unique ID");
	final static IPropertyDescriptor NAME = new TextPropertyDescriptor("name", "Name");
	final static IPropertyDescriptor PARENT_ID = new TextPropertyDescriptor("parent", "Parent ID");
	final static IPropertyDescriptor FILE_NAMES = new TextPropertyDescriptor("file-names", "Associated file names");
	final static IPropertyDescriptor FILE_EXTENSIONS = new TextPropertyDescriptor("file-extensions", "Associated file extensions");
	final static IPropertyDescriptor DEFAULT_CHARSET = new TextPropertyDescriptor("charset", "Default charset");

	final static IPropertyDescriptor[] PROPERTY_DESCRIPTORS = {UNIQUE_ID, NAME, PARENT_ID, FILE_NAMES, FILE_EXTENSIONS, DEFAULT_CHARSET};

	protected IContentType type;

	public ContentTypePropertySource(IContentType type) {
		this.type = type;
	}

	/**
	 * @see org.eclipse.ui.views.properties.IPropertySource#getEditableValue()
	 */
	@Override
	public Object getEditableValue() {
		return null;
	}

	/**
	 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyDescriptors()
	 */
	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return PROPERTY_DESCRIPTORS;
	}

	/**
	 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyValue(Object)
	 */
	@Override
	public Object getPropertyValue(Object id) {
		if (id.equals(UNIQUE_ID.getId()))
			return type.getId();
		if (id.equals(NAME.getId()))
			return type.getName();
		if (id.equals(PARENT_ID.getId())) {
			IContentType baseType = type.getBaseType();
			return baseType == null ? null : baseType.getId();
		}
		if (id.equals(FILE_NAMES.getId()))
			return toString(type.getFileSpecs(IContentType.FILE_NAME_SPEC));
		if (id.equals(FILE_EXTENSIONS.getId()))
			return toString(type.getFileSpecs(IContentType.FILE_EXTENSION_SPEC));
		if (id.equals(DEFAULT_CHARSET.getId()))
			return type.getDefaultCharset();
		return null;
	}

	private Object toString(String[] fileNames) {
		if (fileNames.length == 0)
			return ""; //$NON-NLS-1$
		StringBuilder result = new StringBuilder();
		for (String fileName : fileNames) {
			result.append(fileName);
			result.append(',');
		}
		result.deleteCharAt(result.length() - 1);
		return result.toString();
	}

	/**
	 * @see org.eclipse.ui.views.properties.IPropertySource#isPropertySet(Object)
	 */
	@Override
	public boolean isPropertySet(Object id) {
		return true;
	}

	/**
	 * @see org.eclipse.ui.views.properties.IPropertySource#resetPropertyValue(Object)
	 */
	@Override
	public void resetPropertyValue(Object id) {
		// do nothing
	}

	/**
	 * @see org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(Object, Object)
	 */
	@Override
	public void setPropertyValue(Object id, Object value) {
		// do nothing
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ContentTypePropertySource))
			return false;
		return type.getId().equals(((ContentTypePropertySource) obj).type.getId());
	}

	@Override
	public int hashCode() {
		return type.getId().hashCode();
	}

	@Override
	public String toString() {
		return type.getId();
	}

	@Override
	public Object getAdapter(Class adapter) {
		if (adapter == IPropertySource.class)
			return this;
		return null;
	}
}