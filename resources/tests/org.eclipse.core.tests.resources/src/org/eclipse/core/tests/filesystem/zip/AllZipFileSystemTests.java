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
package org.eclipse.core.tests.filesystem.zip;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Class for collecting all test classes that deal with the file system API.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({ CollapseTest.class, CopyTest.class, CreateTest.class, DeleteTest.class, MoveTest.class,
		RenameTest.class, SetupTest.class, IllegalCompressionMethodTest.class })
public class AllZipFileSystemTests {
}

